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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.marketplace.dto.ListingRequestDto;
import ru.itmo.marketplace.dto.ListingResponseDto;
import ru.itmo.marketplace.dto.ModeratedListingResponseDto;
import ru.itmo.marketplace.dto.ModeratorListingRequestDto;
import ru.itmo.marketplace.entity.Listing;
import ru.itmo.marketplace.entity.ListingStatus;
import ru.itmo.marketplace.mapper.custom.ListingCustomMapper;
import ru.itmo.marketplace.service.ListingFilter;
import ru.itmo.marketplace.service.ListingService;
import ru.itmo.marketplace.service.exceptions.NotFoundException;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class ListingsApiController {
    private final ListingService listingService;
    private final ListingCustomMapper listingMapper;

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/listings",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    public ResponseEntity<ListingResponseDto> createListing(
            @NotNull @RequestHeader(value = "X-User-Id") Long xUserId,
            @Valid @RequestBody ListingRequestDto listingRequestDto
    ) {
        Listing listing = listingMapper.fromDto(listingRequestDto);
        listing = listingService.create(listing, xUserId);
        return ResponseEntity.ok(
                listingMapper.toDto(listing)
        );
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/listings/{id}",
            produces = {"application/json"}
    )
    public ResponseEntity<ListingResponseDto> getListingById(
            @PathVariable("id") Long id
    ) {
        Listing listing = listingService.findById(id).orElseThrow(
                () -> new NotFoundException("Listing with id %s not found".formatted(id))
        );
        return ResponseEntity.ok(
                listingMapper.toDto(listing)
        );
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/listings/{id}",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    public ResponseEntity<ListingResponseDto> updateListing(
            @PathVariable("id") Long id,
            @NotNull @RequestHeader(value = "X-User-Id") Long xUserId,
            @Valid @RequestBody ListingRequestDto listingRequestDto
    ) {
        Listing updatedListing = listingMapper.fromDto(listingRequestDto);
        updatedListing.setId(id);
        updatedListing = listingService.update(updatedListing, xUserId).orElseThrow(
                () -> new NotFoundException("Listing with id %s not found".formatted(id))
        );
        return ResponseEntity.ok(
                listingMapper.toDto(updatedListing)
        );
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/listings/{id}",
            produces = {"application/json"}
    )
    public ResponseEntity<Void> deleteListing(
            @PathVariable("id") Long id,
            @NotNull @RequestHeader(value = "X-User-Id") Long xUserId
    ) {
        if (!listingService.deleteById(id, xUserId)) {
            throw new NotFoundException("Listing with id %s not found".formatted(id));
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/listings",
            produces = {"application/json"}
    )
    public ResponseEntity<Page<ListingResponseDto>> searchListings(
            @Valid @RequestParam(value = "max_price", required = false) Double maxPrice,
            @Valid @RequestParam(value = "min_price", required = false) Double minPrice,
            @Valid @RequestParam(value = "categories_in", required = false) List<Long> categoriesIn,
            @Valid @RequestParam(value = "is_used", required = false) Boolean isUsed,
            Pageable pageable
    ) {
        ListingFilter listingFilter = ListingFilter.builder()
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .categoriesIn(categoriesIn)
                .isUsed(isUsed).build();
        Page<Listing> listings = listingService.findAll(listingFilter, pageable);
        return ResponseEntity.ok(
                listings.map(listingMapper::toDto)
        );
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/listings/open",
            produces = {"application/json"}
    )
    public ResponseEntity<Page<ListingResponseDto>> getOpenListings(
            Pageable pageable
    ) {
        Page<Listing> openListings = listingService.findOpenListings(pageable);
        return ResponseEntity.ok(
                openListings.map(listingMapper::toDto)
        );
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/listings/{id}/status",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    public ResponseEntity<ModeratedListingResponseDto> updateListingStatus(
            @PathVariable("id") Long id,
            @NotNull @RequestHeader(value = "X-User-Id") Long xUserId,
            @Valid @RequestBody ModeratorListingRequestDto moderatorListingRequestDto
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
