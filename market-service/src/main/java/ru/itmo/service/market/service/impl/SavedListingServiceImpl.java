package ru.itmo.service.market.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.common.exception.DuplicateException;
import ru.itmo.common.exception.NotFoundException;
import ru.itmo.service.market.entity.Listing;
import ru.itmo.service.market.entity.SavedListing;
import ru.itmo.service.market.repository.SavedListingRepository;
import ru.itmo.service.market.service.ListingService;
import ru.itmo.service.market.service.SavedListingService;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SavedListingServiceImpl implements SavedListingService {
    private final SavedListingRepository savedListingRepository;
    private final ListingService listingService;

    @Override
    @Transactional(readOnly = true)
    public Optional<SavedListing> findById(Long listingId, Long userId) {
        return savedListingRepository.findByListingIdAndUserId(listingId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SavedListing> findAll(Long userId, Pageable pageable) {
        return savedListingRepository.findAllByUserId(userId, pageable);
    }

    @Override
    @Transactional
    public SavedListing create(SavedListing entity) {
        Listing listing = listingService.findById(entity.getListingId())
                .orElseThrow(() -> new NotFoundException("Listing not found"));
        Optional<SavedListing> existingSavedListing = savedListingRepository
                .findByUserIdAndListingId(entity.getUserId(), entity.getListingId());

        if (existingSavedListing.isPresent()) {
            throw new DuplicateException("This listing is already saved by the user.");
        }
        entity.setListing(listing);
        return savedListingRepository.save(entity);
    }

    @Override
    @Transactional
    public boolean deleteById(Long listingId, Long userId) {
        int deletedCount = savedListingRepository.deleteByListingIdAndUserId(listingId, userId);
        return deletedCount > 0;
    }
}
