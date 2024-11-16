package ru.itmo.common.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.itmo.common.model.Listing;
import ru.itmo.common.model.ListingStatus;

public interface ListingService {

    Optional<Listing> findById(Long id);

    Page<Listing> findAll(ListingFilter listingFilter, Pageable pageable);

    Listing create(Listing listing, Long userId);

    Optional<Listing> update(Listing listing, Long userId);

    Optional<Listing> updateStatusById(ListingStatus listingStatus, Long id);

    boolean deleteById(Long listingId, Long userId);

    Page<Listing> findOpenListings(Pageable pageable);

    Page<Listing> findByUserIdAndStatus(Long userId, ListingStatus status, Pageable pageable);
}
