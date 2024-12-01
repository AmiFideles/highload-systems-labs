package ru.itmo.service.market.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.CollectionFormat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.itmo.common.dto.listing.ListingRequestDto;
import ru.itmo.common.dto.listing.ListingResponseDto;
import ru.itmo.common.dto.listing.ModeratedListingResponseDto;
import ru.itmo.common.dto.listing.ModeratorListingRequestDto;

import java.util.List;

@ReactiveFeignClient(name = "market-service", path = "/api/v1/listings")
public interface ListingApiReactiveClient {

    @PostMapping(
            value = "",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    Mono<ListingResponseDto> createListing(
            @Valid @RequestBody ListingRequestDto listingRequestDto
    );

    @GetMapping(
            value = "/{id}",
            produces = {"application/json"}
    )
    Mono<ListingResponseDto> getListingById(
            @PathVariable("id") Long id
    );

    @GetMapping(
            value = "",
            produces = {"application/json"}
    )
    @CollectionFormat(feign.CollectionFormat.CSV)
    Flux<ListingResponseDto> getListingsByIds(
            @RequestParam("ids") List<Long> ids
    );

    @GetMapping(
            value = "",
            produces = {"application/json"}
    )
    Mono<Page<ListingResponseDto>> searchListings(
            @Valid @RequestParam(value = "max_price", required = false) Double maxPrice,
            @Valid @RequestParam(value = "min_price", required = false) Double minPrice,
            @Valid @RequestParam(value = "categories_in", required = false) List<Long> categoriesIn,
            @Valid @RequestParam(value = "is_used", required = false) Boolean isUsed,
            Pageable pageable
    );


    @PutMapping(
            value = "/{id}",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    Mono<ListingResponseDto> updateListing(
            @PathVariable("id") Long id,
            @Valid @RequestBody ListingRequestDto listingRequestDto
    );

    @DeleteMapping(
            value = "/{id}",
            produces = {"application/json"}
    )
    Mono<Void> deleteListing(
            @PathVariable("id") Long id
    );

    @GetMapping(
            value = "/open",
            produces = {"application/json"}
    )
    Mono<Page<ListingResponseDto>> getOpenListings(
            Pageable pageable
    );

    @PutMapping(
            value = "/{id}/status",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    Mono<ModeratedListingResponseDto> updateListingStatus(
            @PathVariable("id") Long id,
            @Valid @RequestBody ModeratorListingRequestDto moderatorListingRequestDto
    );

}
