package ru.itmo.marketplace.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.marketplace.entity.Listing;
import ru.itmo.marketplace.entity.SavedListing;
import ru.itmo.marketplace.entity.User;
import ru.itmo.marketplace.repository.SavedListingRepository;
import ru.itmo.marketplace.service.ListingService;
import ru.itmo.marketplace.service.SavedListingService;
import ru.itmo.marketplace.service.UserService;
import ru.itmo.marketplace.service.exceptions.DuplicateException;
import ru.itmo.marketplace.service.exceptions.NotFoundException;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SavedListingServiceImpl implements SavedListingService {
    private final SavedListingRepository savedListingRepository;
    private final UserService userService;
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
        User user = userService.findById(entity.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        Listing listing = listingService.findById(entity.getListingId())
                .orElseThrow(() -> new NotFoundException("Listing not found"));
        Optional<SavedListing> existingSavedListing = savedListingRepository
                .findByUserIdAndListingId(entity.getUserId(), entity.getListingId());

        if (existingSavedListing.isPresent()) {
                throw new DuplicateException("This listing is already saved by the user.");
        }

        entity.setUser(user);
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
