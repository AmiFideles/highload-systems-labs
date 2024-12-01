package ru.itmo.service.market.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.common.dto.saved.SavedListingRequestDto;
import ru.itmo.common.dto.saved.SavedListingResponseDto;
import ru.itmo.common.exception.NotFoundException;
import ru.itmo.modules.security.InternalAuthentication;
import ru.itmo.service.market.entity.SavedListing;
import ru.itmo.service.market.mapper.custom.SavedListingCustomMapper;
import ru.itmo.service.market.service.SavedListingService;


@Slf4j
@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class SavedListingsApiController {
    private final SavedListingCustomMapper savedListingMapper;
    private final SavedListingService savedListingService;

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/saved-listings",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    public ResponseEntity<SavedListingResponseDto> addSavedListing(
            @Valid @RequestBody SavedListingRequestDto savedListingRequestDto,
            InternalAuthentication currentUser
    ) {
        SavedListing savedListing = savedListingMapper.fromDto(savedListingRequestDto);
        savedListing.setUserId(currentUser.getUserId());

        savedListing = savedListingService.create(savedListing);

        return ResponseEntity.ok(
                savedListingMapper.toDto(savedListing)
        );
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/saved-listings/{listing_id}",
            produces = {"application/json"}
    )
    public ResponseEntity<SavedListingResponseDto> getSavedListingById(
            @PathVariable("listing_id") Long listingId,
            InternalAuthentication currentUser
    ) {
        SavedListing savedListing = savedListingService.findById(listingId, currentUser.getUserId()).orElseThrow(
                () -> new NotFoundException("Saved listing with id %s not found".formatted(listingId))
        );
        return ResponseEntity.ok(
                savedListingMapper.toDto(savedListing)
        );
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/saved-listings",
            produces = {"application/json"}
    )
    public ResponseEntity<Page<SavedListingResponseDto>> getSavedListings(
            Pageable pageable,
            InternalAuthentication currentUser
    ) {
        Page<SavedListing> savedListings = savedListingService.findAll(currentUser.getUserId(), pageable);
        return ResponseEntity.ok(
                savedListings.map(savedListingMapper::toDto)
        );
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/saved-listings/{listing_id}",
            produces = {"application/json"}
    )
    public ResponseEntity<Void> deleteSavedListing(
            @PathVariable("listing_id") Long listingId,
            InternalAuthentication currentUser
    ) {
        boolean deleted = savedListingService.deleteById(listingId, currentUser.getUserId());
        if (!deleted) {
            throw new NotFoundException("Saved listing with id %s not found".formatted(listingId));
        }
        return ResponseEntity.ok().build();
    }
}
