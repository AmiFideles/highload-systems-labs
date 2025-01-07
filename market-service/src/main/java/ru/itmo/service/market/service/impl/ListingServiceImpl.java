package ru.itmo.service.market.service.impl;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.common.dto.listing.ListingStatusChangedNotificationDto;
import ru.itmo.common.dto.listing.ListingUnavailableNotificationDto;
import ru.itmo.common.dto.user.UserResponseDto;
import ru.itmo.common.exception.AccessDeniedException;
import ru.itmo.common.exception.NotFoundException;
import ru.itmo.modules.security.UserSecurityContextHolder;
import ru.itmo.service.market.entity.Listing;
import ru.itmo.service.market.entity.ListingStatus;
import ru.itmo.service.market.mapper.mapstruct.ListingMapper;
import ru.itmo.service.market.producer.KafkaProducerService;
import ru.itmo.service.market.repository.ListingRepository;
import ru.itmo.service.market.service.ListingFilter;
import ru.itmo.service.market.service.ListingService;
import ru.itmo.service.market.service.SavedListingService;
import ru.itmo.service.user.client.UserApiClient;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ListingServiceImpl implements ListingService {
    private final ListingRepository listingRepository;
    private final UserApiClient userClient;
    private final UserSecurityContextHolder contextHolder;
    private final ListingMapper listingMapper;
    private final KafkaProducerService kafkaProducerService;
    private SavedListingService savedListingService;
    @Value("${app.kafka.topics.listing-status-updated}")
    private String listingUpdatedTopic;
    @Value("${app.kafka.topics.saved-listing-unavailable}")
    private String listingUnavailableTopic;


    @Override
    @Transactional(readOnly = true)
    public Optional<Listing> findById(Long id) {
        return listingRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Listing> findAll(ListingFilter listingFilter, Pageable pageable) {
        Specification<Listing> specification = ListingSpecification.createListingSpecification(listingFilter);
        return listingRepository.findAll(specification, pageable);
    }

    @Override
    @Transactional
    public Listing create(Listing listing, Long userId) {
        listing.setCreatorId(userId);
        listing.setStatus(ListingStatus.REVIEW);
        return listingRepository.save(listing);
    }

    @Override
    @Transactional
    public Optional<Listing> update(Listing listing, Long userId) {
        Optional<Listing> listingOptional = listingRepository.findById(listing.getId());
        if (listingOptional.isPresent()) {
            Listing saved = listingOptional.get();
            Long creatorId = saved.getCreatorId();
            if (!creatorId.equals(userId)) {
                throw new AccessDeniedException("User does not have permission to modify this listing.");
            } else {
                listing.setId(saved.getId());
                listing.setCreatorId(userId);
                listing.setCreatedAt(saved.getCreatedAt());
                Optional<Listing> result = Optional.of(listingRepository.save(listing));
                if(listing.getStatus().equals(ListingStatus.CLOSED)){
                    List<Long> userIds = savedListingService.findByListingId(listing.getId());
                    kafkaProducerService.sendMessage(listingUnavailableTopic, new ListingUnavailableNotificationDto(userIds, listing.getTitle()));
                }
                return result;
            }
        } else {
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public boolean deleteById(Long listingId, Long userId) {
        Listing listing = listingRepository.findById(listingId).orElseThrow(
                () -> new NotFoundException("Listing not found with id: " + listingId)
        );

        if (!listing.getCreatorId().equals(userId)) {
            throw new AccessDeniedException("User does not have permission to delete this listing.");
        }

        return listingRepository.removeById(listingId) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Listing> findOpenListings(Pageable pageable) {
        return listingRepository.findByStatus(ListingStatus.OPEN, pageable);
    }

    @Override
    @Transactional
    public Page<Listing> findByUserIdAndStatus(Long userId, @Nullable ListingStatus status, Pageable pageable) {
        try {
            UserResponseDto user = userClient.getUserById(
                    userId,
                    contextHolder.getUserId(),
                    contextHolder.getUserRole()
            );
        } catch (FeignException.NotFound e) {
            throw new NotFoundException("User with id %s not found".formatted(userId));
        }

        if (status == null) {
            return listingRepository.findByCreatorId(userId, pageable);
        }
        return listingRepository.findByStatusAndCreatorId(status, userId, pageable);
    }

    @Override
    @Transactional
    public Optional<Listing> updateStatusById(ListingStatus listingStatus, Long id) {
        Optional<Listing> listingOptional = listingRepository.findById(id);
        if (listingOptional.isPresent()) {
            Listing listing = listingOptional.get();
            listing.setStatus(listingStatus);
            ListingStatusChangedNotificationDto listingStatusChangedNotificationDto = ListingStatusChangedNotificationDto.builder()
                    .creatorId(listing.getCreatorId())
                    .listingStatusDto(listingMapper.toDto(listingStatus))
                    .title(listing.getTitle())
                    .build();
            kafkaProducerService.sendMessage(listingUpdatedTopic, listingStatusChangedNotificationDto);
            return Optional.of(listingRepository.save(listing));
        } else {
            return Optional.empty();
        }
    }

    @Autowired
    public void setSavedListingService(@Lazy SavedListingService savedListingService) {
        this.savedListingService = savedListingService;
    }
}
