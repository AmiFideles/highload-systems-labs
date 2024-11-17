package ru.itmo.marketplace.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.marketplace.entity.Listing;
import ru.itmo.marketplace.entity.ListingStatus;
import ru.itmo.marketplace.entity.Role;
import ru.itmo.marketplace.entity.User;
import ru.itmo.marketplace.repository.ListingRepository;
import ru.itmo.marketplace.service.ListingFilter;
import ru.itmo.marketplace.service.ListingService;
import ru.itmo.marketplace.service.ListingSpecification;
import ru.itmo.marketplace.service.UserService;
import ru.itmo.marketplace.service.exceptions.AccessDeniedException;
import ru.itmo.marketplace.service.exceptions.NotFoundException;

import java.util.HashSet;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ListingServiceImpl implements ListingService {
    private final ListingRepository listingRepository;
    private final UserService userService;

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
        User user = userService.findById(userId).orElseThrow(
                () -> new NotFoundException("User not found with id: " + userId)
        );
        if (!user.getRole().equals(Role.SELLER)) {
            throw new AccessDeniedException("Only sellers can create listings.");
        }
        listing.setCreator(user);
        return listingRepository.save(listing);
    }

    @Override
    @Transactional
    public Optional<Listing> update(Listing listing, Long userId) {
        User user = userService.findById(userId).orElseThrow(
                () -> new NotFoundException("User not found with id: " + userId)
        );
        Optional<Listing> byId = listingRepository.findById(listing.getId());
        if (byId.isPresent()) {
            Listing saved = byId.get();
            User creator = saved.getCreator();
            if (!creator.getId().equals(user.getId())) {
                throw new AccessDeniedException("User does not have permission to modify this listing.");
            } else {

                saved.setCategories(new HashSet<>(listing.getCategories()));
                saved.setTitle(listing.getTitle());
                saved.setDescription(listing.getDescription());
                saved.setPrice(listing.getPrice());
                saved.setStatus(listing.getStatus());
                saved.setUsed(listing.isUsed());
                saved.setCreator(user);
                return Optional.of(listingRepository.save(saved));
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

        if (!listing.getCreator().getId().equals(userId)) {
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
        if (!userService.existsById(userId)) {
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
            return Optional.of(listingRepository.save(listing));
        } else {
            return Optional.empty();
        }
    }
}
