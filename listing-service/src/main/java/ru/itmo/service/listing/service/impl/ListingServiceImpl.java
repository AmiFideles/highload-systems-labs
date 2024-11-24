package ru.itmo.service.listing.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.itmo.common.exception.AccessDeniedException;
import ru.itmo.common.exception.NotFoundException;
import ru.itmo.service.listing.client.UserServiceReactiveClient;
import ru.itmo.service.listing.entity.Listing;
import ru.itmo.service.listing.entity.ListingStatus;
import ru.itmo.service.listing.repository.ListingRepository;
import ru.itmo.service.listing.service.ListingFilter;
import ru.itmo.service.listing.service.ListingService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ListingServiceImpl implements ListingService {
    private final ListingRepository listingRepository;
    private final UserServiceReactiveClient userClient;

    @Override
    public Mono<Listing> findById(Long id) {
        return listingRepository.findById(id);
    }

    @Override
    public Mono<Page<Listing>> findAll(ListingFilter listingFilter, Pageable pageable) {
        return listingRepository.findFilteredListings(
                listingFilter.getMinPrice(),
                listingFilter.getMaxPrice(),
                listingFilter.getIsUsed(),
                pageable)
                .collectList()
                .zipWith(listingRepository.count())
                .map(t -> new PageImpl<>(t.getT1(), pageable, t.getT2()));
    }


    @Override
    public Mono<Listing> create(Listing listing, Long userId) {
        listing.setCreatorId(userId);
        return listingRepository.save(listing);
    }


    @Override
    public Mono<Listing> update(Listing listing, Long userId) {
        return listingRepository.findById(listing.getId())
                .switchIfEmpty(Mono.error(new NotFoundException("Listing not found with id: " + listing.getId())))
                .flatMap(saved -> {
                    if (!userId.equals(saved.getCreatorId())) {
                        return Mono.error(new AccessDeniedException("User does not have permission to modify this listing."));
                    }
                    saved.setTitle(listing.getTitle());
                    saved.setDescription(listing.getDescription());
                    saved.setPrice(listing.getPrice());
                    saved.setStatus(listing.getStatus());
                    saved.setUsed(listing.isUsed());
                    return listingRepository.save(saved);
                });
    }

    @Override
    public Mono<Boolean> deleteById(Long listingId, Long userId) {
        return listingRepository.findById(listingId)
                .switchIfEmpty(Mono.error(new NotFoundException("Listing not found with id: " + listingId)))
                .flatMap(listing -> {
                    if (!userId.equals(listing.getCreatorId())) {
                        return Mono.error(new AccessDeniedException("User does not have permission to delete this listing."));
                    }
                    return listingRepository.removeById(listingId)
                            .map(result -> result > 0);
                });
    }

    @Override
    public Mono<Page<Listing>> findOpenListings(Pageable pageable) {
        return listingRepository.findByStatus(ListingStatus.OPEN, pageable)
                .collectList()
                .zipWith(listingRepository.count())
                .map(t -> new PageImpl<>(t.getT1(), pageable, t.getT2()));
    }

    @Override
    public Mono<Page<Listing>> findByUserIdAndStatus(Long userId, @Nullable ListingStatus status, Pageable pageable) {
        return userClient.getUserById(userId)
                .switchIfEmpty(Mono.error(new NotFoundException("User with id %s not found".formatted(userId))))
                .flatMapMany(user -> {
                    if (status == null) {
                        return listingRepository.findByCreatorId(userId, pageable);
                    } else {
                        return listingRepository.findByStatusAndCreatorId(status, userId, pageable);
                    }
                })
                .collectList()
                .zipWith(listingRepository.count())
                .map(t -> new PageImpl<>(t.getT1(), pageable, t.getT2()));
    }

    @Override
    public Mono<Listing> updateStatusById(ListingStatus listingStatus, Long id) {
        return listingRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Listing not found with id: " + id)))
                .flatMap(listing -> {
                    listing.setStatus(listingStatus);
                    return listingRepository.save(listing);
                });
    }
}
