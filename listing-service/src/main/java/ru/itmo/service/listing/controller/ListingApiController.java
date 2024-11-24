package ru.itmo.service.listing.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.itmo.common.dto.listing.ListingRequestDto;
import ru.itmo.common.dto.listing.ListingResponseDto;
import ru.itmo.common.dto.listing.ModeratedListingResponseDto;
import ru.itmo.common.dto.listing.ModeratorListingRequestDto;
import ru.itmo.common.exception.NotFoundException;
import ru.itmo.modules.security.InternalAuthentication;
import ru.itmo.service.listing.entity.Listing;
import ru.itmo.service.listing.entity.ListingStatus;
import ru.itmo.service.listing.mapper.ListingMapper;
import ru.itmo.service.listing.service.ListingFilter;
import ru.itmo.service.listing.service.ListingService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/listings")
@RequiredArgsConstructor
public class ListingApiController {
    private final ListingService listingService;
    private final ListingMapper listingMapper;

    @PostMapping(
            produces = "application/json",
            consumes = "application/json"
    )
    @PreAuthorize("hasAuthority('SELLER')")
    public Mono<ResponseEntity<ListingResponseDto>> createListing(
            @RequestBody ListingRequestDto listingRequestDto,
            InternalAuthentication currentUser
    ) {
        Long userId = currentUser.getUserId();
        Listing listing = listingMapper.fromDto(listingRequestDto);
        return listingService.create(listing, userId)
                .map(savedListing -> ResponseEntity.ok(listingMapper.toDto(savedListing)));
    }

    @GetMapping(
            value = "/{id}",
            produces = "application/json",
            consumes = "application/json"
    )
    public Mono<ResponseEntity<ListingResponseDto>> getListingById(
            @PathVariable("id") Long id
    ) {
        return listingService.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Listing with id %s not found".formatted(id))))
                .map(listing -> ResponseEntity.ok(listingMapper.toDto(listing)));
    }

    @PutMapping(
            value = "/{id}",
            produces = "application/json",
            consumes = "application/json"
    )
    @PreAuthorize("hasAuthority('SELLER')")
    public Mono<ResponseEntity<ListingResponseDto>> updateListing(
            @PathVariable("id") Long id,
            @RequestBody ListingRequestDto listingRequestDto,
            InternalAuthentication currentUser

    ) {
        Listing updatedListing = listingMapper.fromDto(listingRequestDto);
        updatedListing.setId(id);
        Long userId = currentUser.getUserId();
        return listingService.update(updatedListing, userId)
                .map(savedListing -> ResponseEntity.ok(listingMapper.toDto(savedListing)))
                .switchIfEmpty(Mono.error(new NotFoundException("Listing with id %s not found".formatted(id))));

    }

    @DeleteMapping(
            value = "/{id}",
            produces = "application/json"
    )
    @PreAuthorize("hasAuthority('SELLER')")
    public Mono<ResponseEntity<Boolean>> deleteListing(
            @PathVariable("id") Long id,
            InternalAuthentication currentUser
    ) {
        Long userId = currentUser.getUserId();
        return listingService.deleteById(id, userId)
                .switchIfEmpty(Mono.error(new NotFoundException("Listing with id %s not found".formatted(id))))
                .map(ResponseEntity::ok);
    }

    @GetMapping(
            value = "",
            produces = "application/json",
            consumes = "application/json"
    )
    public Mono<ResponseEntity<Page<ListingResponseDto>>> searchListings(
            @RequestParam(value = "max_price", required = false) Double maxPrice,
            @RequestParam(value = "min_price", required = false) Double minPrice,
            @RequestParam(value = "categories_in", required = false) List<Long> categoriesIn,
            @RequestParam(value = "is_used", required = false) Boolean isUsed,
            Pageable pageable
    ) {
        ListingFilter listingFilter = ListingFilter.builder()
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .categoriesIn(categoriesIn)
                .isUsed(isUsed).build();
        return listingService.findAll(listingFilter, pageable)
                .map(listings -> ResponseEntity.ok(
                        listings.map(listingMapper::toDto)
                ));
    }

    @GetMapping(
            value = "/open",
            produces = "application/json",
            consumes = "application/json"
    )
    @PreAuthorize("hasAuthority('MODERATOR') or hasAuthority('ADMIN')")
    public Mono<ResponseEntity<Page<ListingResponseDto>>> getOpenListings(
            Pageable pageable
    ) {
        return listingService.findOpenListings(pageable)
                .map(openListings -> ResponseEntity.ok(
                        openListings.map(listingMapper::toDto)
                ));
    }

    @PutMapping(
            value = "/{id}/status",
            produces = "application/json",
            consumes = "application/json"
    )
    @PreAuthorize("hasAuthority('MODERATOR') or hasAuthority('ADMIN')")
    public Mono<ResponseEntity<ModeratedListingResponseDto>> updateListingStatus(
            @PathVariable("id") Long id,
            @RequestBody ModeratorListingRequestDto moderatorListingRequestDto
    ) {
        ListingStatus listingStatus = listingMapper.fromDto(moderatorListingRequestDto.getStatus());
        return listingService.updateStatusById(listingStatus, id)
                .map(updatedListing -> ResponseEntity.ok(listingMapper.toModeratedDto(updatedListing)));
    }
}
