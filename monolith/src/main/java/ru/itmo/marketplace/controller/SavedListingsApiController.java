package ru.itmo.marketplace.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.marketplace.api.SavedListingsApi;
import ru.itmo.marketplace.entity.SavedListing;
import ru.itmo.marketplace.mapper.custom.SavedListingCustomMapper;
import ru.itmo.marketplace.model.SavedListingPageableResponseDto;
import ru.itmo.marketplace.model.SavedListingRequestDto;
import ru.itmo.marketplace.model.SavedListingResponseDto;
import ru.itmo.marketplace.service.SavedListingService;
import ru.itmo.marketplace.service.exceptions.NotFoundException;

@Slf4j
@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class SavedListingsApiController implements SavedListingsApi {
    private final SavedListingCustomMapper savedListingMapper;
    private final SavedListingService savedListingService;


    @Override
    public ResponseEntity<SavedListingResponseDto> addSavedListing(Long userId, SavedListingRequestDto savedListingRequestDto) {
        SavedListing savedListing = savedListingMapper.fromDto(savedListingRequestDto);
        savedListing.setUserId(userId);

        savedListing = savedListingService.create(savedListing);

        return ResponseEntity.ok(
                savedListingMapper.toDto(savedListing)
        );
    }

    @Override
    public ResponseEntity<SavedListingResponseDto> getSavedListingById(Long listingId, Long userId) {
        SavedListing savedListing = savedListingService.findById(listingId, userId).orElseThrow(
                () -> new NotFoundException("Saved listing with id %s not found".formatted(listingId))
        );
        return ResponseEntity.ok(
                savedListingMapper.toDto(savedListing)
        );
    }

    @Override
    public ResponseEntity<SavedListingPageableResponseDto> getSavedListings(Long userId, Pageable pageable) {
        Page<SavedListing> savedListings = savedListingService.findAll(userId, pageable);
        return ResponseEntity.ok(savedListingMapper.toDto(savedListings));
    }

    @Override
    public ResponseEntity<Void> deleteSavedListing(Long listingId, Long userId) {
        boolean deleted = savedListingService.deleteById(listingId, userId);
        if (!deleted) {
            throw new NotFoundException("Saved listing with id %s not found".formatted(listingId));
        }
        return ResponseEntity.ok().build();
    }
}
