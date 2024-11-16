package ru.itmo.common.service;


import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.itmo.common.model.SavedListing;

public interface SavedListingService {
    Optional<SavedListing> findById(Long listingId, Long userId);

    Page<SavedListing> findAll(Long userId, Pageable pageable);

    SavedListing create(SavedListing entity);

    boolean deleteById(Long listingId, Long userId);
}
