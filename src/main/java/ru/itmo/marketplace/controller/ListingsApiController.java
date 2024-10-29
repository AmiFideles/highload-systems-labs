package ru.itmo.marketplace.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.marketplace.api.ListingsApi;
import ru.itmo.marketplace.entity.Listing;
import ru.itmo.marketplace.entity.ListingStatus;
import ru.itmo.marketplace.mapper.custom.ListingCustomMapper;
import ru.itmo.marketplace.model.*;
import ru.itmo.marketplace.service.ListingFilter;
import ru.itmo.marketplace.service.ListingService;
import ru.itmo.marketplace.service.exceptions.NotFoundException;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class ListingsApiController implements ListingsApi {
    private final ListingService listingService;
    private final ListingCustomMapper listingMapper;

    @Override
    public ResponseEntity<ListingResponseDto> createListing(Long userId, ListingRequestDto listingRequestDto) {
        Listing listing = listingMapper.fromDto(listingRequestDto);
        listing = listingService.create(listing, userId);
        return ResponseEntity.ok(
                listingMapper.toDto(listing)
        );
    }

    @Override
    public ResponseEntity<ListingResponseDto> getListingById(Long id) {
        Listing listing = listingService.findById(id).orElseThrow(
                () -> new NotFoundException("Listing with id %s not found".formatted(id))
        );
        return ResponseEntity.ok(
                listingMapper.toDto(listing)
        );
    }

    @Override
    public ResponseEntity<ListingResponseDto> updateListing(
            Long id, Long userId, ListingRequestDto listingRequestDto
    ) {
        Listing updatedListing = listingMapper.fromDto(listingRequestDto);
        updatedListing.setId(id);
        updatedListing = listingService.update(updatedListing, userId).orElseThrow(
                () -> new NotFoundException("Listing with id %s not found".formatted(id))
        );
        return ResponseEntity.ok(
                listingMapper.toDto(updatedListing)
        );
    }

    @Override
    public ResponseEntity<Void> deleteListing(Long id, Long userId) {
        if (!listingService.deleteById(id, userId)) {
            throw new NotFoundException("Listing with id %s not found".formatted(id));
        }
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<ListingPageableResponseDto> searchListings(Double maxPrice, Double minPrice, List<Long> categoriesIn, Boolean isUsed, Pageable pageable) {
        ListingFilter listingFilter = ListingFilter.builder()
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .categoriesIn(categoriesIn)
                .isUsed(isUsed).build();
        Page<Listing> listings = listingService.findAll(listingFilter, pageable);
        return ResponseEntity.ok(listingMapper.toDto(listings));
    }

    @Override
    public ResponseEntity<ListingPageableResponseDto> getOpenListings(Pageable pageable) {
        Page<Listing> openListings = listingService.findOpenListings(pageable);
        return ResponseEntity.ok(listingMapper.toDto(openListings));
    }

    @Override
    public ResponseEntity<ModeratedListingResponseDto> updateListingStatus(
            Long id, Long UserId, ModeratorListingRequestDto moderatorListingRequestDto
    ) {
        ListingStatus listingStatus = listingMapper.fromDto(moderatorListingRequestDto.getStatus());
        Optional<Listing> updatedListing = listingService.updateStatusById(listingStatus, id);
        updatedListing.orElseThrow(
                () -> new NotFoundException("Listing with id %s not found".formatted(id))
        );
        return ResponseEntity.ok(
                listingMapper.toModeratedDto(updatedListing.get())
        );
    }
}
