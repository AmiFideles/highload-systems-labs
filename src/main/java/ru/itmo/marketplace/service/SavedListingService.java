package ru.itmo.marketplace.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.itmo.marketplace.entity.SavedListing;

import java.util.Optional;

public interface SavedListingService {
    Optional<SavedListing> findById(Long listingId, Long userId);

    Page<SavedListing> findAll(Long userId, Pageable pageable);

    SavedListing create(SavedListing entity);

    boolean deleteById(Long listingId, Long userId);
}
