package ru.itmo.marketplace.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.marketplace.entity.SavedListing;
import ru.itmo.marketplace.mapper.custom.SavedListingCustomMapper;
import ru.itmo.marketplace.dto.SavedListingRequestDto;
import ru.itmo.marketplace.dto.SavedListingResponseDto;
import ru.itmo.marketplace.service.SavedListingService;
import ru.itmo.marketplace.service.exceptions.NotFoundException;

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
            @NotNull @RequestHeader(value = "X-User-Id") Long xUserId,
            @Valid @RequestBody SavedListingRequestDto savedListingRequestDto
    ) {
        SavedListing savedListing = savedListingMapper.fromDto(savedListingRequestDto);
        savedListing.setUserId(xUserId);

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
            @NotNull @RequestHeader(value = "X-User-Id") Long xUserId
    ) {
        SavedListing savedListing = savedListingService.findById(listingId, xUserId).orElseThrow(
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
            @NotNull @RequestHeader(value = "X-User-Id") Long xUserId,
            Pageable pageable
    ) {
        Page<SavedListing> savedListings = savedListingService.findAll(xUserId, pageable);
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
            @NotNull @RequestHeader(value = "X-User-Id") Long xUserId
    ) {
        boolean deleted = savedListingService.deleteById(listingId, xUserId);
        if (!deleted) {
            throw new NotFoundException("Saved listing with id %s not found".formatted(listingId));
        }
        return ResponseEntity.ok().build();
    }
}
