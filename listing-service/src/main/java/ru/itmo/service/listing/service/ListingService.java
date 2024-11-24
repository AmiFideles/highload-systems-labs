package ru.itmo.service.listing.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;
import ru.itmo.service.listing.entity.Listing;
import ru.itmo.service.listing.entity.ListingStatus;

public interface ListingService {
    Mono<Listing> findById(Long id);

    Mono<Page<Listing>> findAll(ListingFilter listingFilter, Pageable pageable);

    Mono<Listing> create(Listing listing, Long userId);

    Mono<Listing> update(Listing listing, Long userId);

    Mono<Boolean> deleteById(Long listingId, Long userId);

    Mono<Page<Listing>> findOpenListings(Pageable pageable);

    Mono<Page<Listing>> findByUserIdAndStatus(Long userId, ListingStatus status, Pageable pageable);

    Mono<Listing> updateStatusById(ListingStatus listingStatus, Long id);
}
