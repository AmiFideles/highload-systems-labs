package ru.itmo.service.listing.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.itmo.common.dto.listing.ListingRequestDto;
import ru.itmo.common.dto.listing.ListingResponseDto;
import ru.itmo.service.listing.client.UserServiceReactiveClient;
import ru.itmo.service.listing.entity.Listing;
import ru.itmo.service.listing.mapper.ListingMapper;
import ru.itmo.service.listing.repository.ListingRepository;
import ru.itmo.service.listing.service.ListingService;

@Slf4j
@RestController
@RequestMapping("/api/v1/listings")
@RequiredArgsConstructor
public class ListingApiController {
    private final ListingRepository listingRepository;
    private final UserServiceReactiveClient userClient;
    private final ListingService listingService;
    private final ListingMapper listingMapper;


    @PostMapping(
            produces = "application/json",
            consumes = "application/json"
    )
    public Mono<ResponseEntity<ListingResponseDto>> createListing(
            @RequestHeader("X-User-Id") Long xUserId,
            @RequestBody ListingRequestDto listingRequestDto
    ) {
        Listing listing = listingMapper.fromDto(listingRequestDto);
        return listingService.create(listing, xUserId)
                .map(createdListing -> ResponseEntity.ok(listingMapper.toDto(createdListing)));
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public Mono<ResponseEntity<ListingResponseDto>> getListingById(
            @PathVariable("id") Long id
    ) {
        return listingService.findById(id)
                .map(listing -> ResponseEntity.ok(listingMapper.toDto(listing)))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @PutMapping(value = "/{id}", produces = "application/json", consumes = "application/json")
    public Mono<ResponseEntity<ListingResponseDto>> updateListing(
            @PathVariable("id") Long id,
            @RequestHeader("X-User-Id") Long xUserId,
            @RequestBody ListingRequestDto listingRequestDto
    ) {
        Listing updatedListing = listingMapper.fromDto(listingRequestDto);
        updatedListing.setId(id);
        return listingService.update(updatedListing, xUserId)
                .map(updated -> ResponseEntity.ok(listingMapper.toDto(updated)))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @DeleteMapping(value = "/{id}", produces = "application/json")
    public Mono<ResponseEntity<Void>> deleteListing(
            @PathVariable("id") Long id,
            @RequestHeader("X-User-Id") Long xUserId
    ) {
        return listingService.deleteById(id, xUserId)
                .flatMap(deleted -> deleted
                        ? Mono.just(ResponseEntity.ok().build())
                        : Mono.just(ResponseEntity.notFound().build())
                );
    }

//    @GetMapping(value = "", produces = "application/json")
//    public Mono<ResponseEntity<Page<ListingResponseDto>>> searchListings(
//            @RequestParam(value = "max_price", required = false) Double maxPrice,
//            @RequestParam(value = "min_price", required = false) Double minPrice,
//            @RequestParam(value = "categories_in", required = false) List<Long> categoriesIn,
//            @RequestParam(value = "is_used", required = false) Boolean isUsed,
//            Pageable pageable
//    ) {
//        ListingFilter listingFilter = ListingFilter.builder()
//                .minPrice(minPrice)
//                .maxPrice(maxPrice)
//                .categoriesIn(categoriesIn)
//                .isUsed(isUsed)
//                .build();
//
//        return listingService.findAll(listingFilter, pageable)
//                .collectList()
//                .zipWith(listingService.countAll(listingFilter))
//                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()))
//                .map(page -> ResponseEntity.ok(page.map(listingMapper::toDto)));
//    }
//
//    @GetMapping(value = "/open", produces = "application/json")
//    public Mono<ResponseEntity<Page<ListingResponseDto>>> getOpenListings(
//            Pageable pageable
//    ) {
//        return listingService.findOpenListings(pageable)
//                .collectList()
//                .zipWith(listingService.countOpenListings())
//                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()))
//                .map(page -> ResponseEntity.ok(page.map(listingMapper::toDto)));
//    }
//
//    @PutMapping(value = "/{id}/status", produces = "application/json", consumes = "application/json")
//    public Mono<ResponseEntity<ModeratedListingResponseDto>> updateListingStatus(
//            @PathVariable("id") Long id,
//            @RequestHeader("X-User-Id") Long xUserId,
//            @RequestBody ModeratorListingRequestDto moderatorListingRequestDto
//    ) {
//        ListingStatus listingStatus = listingMapper.fromDto(moderatorListingRequestDto.getStatus());
//        return listingService.updateStatusById(listingStatus, id)
//                .map(updated -> ResponseEntity.ok(listingMapper.toModeratedDto(updated)))
//                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
//    }
//
//    @GetMapping(value = "/users/{id}", produces = "application/json")
//    public Mono<ResponseEntity<UserResponseDto>> getUserById(@PathVariable("id") Long id) {
//        return userClient.getUserById(id)
//                .map(ResponseEntity::ok)
//                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
//                .doOnError(ex -> log.error("Error retrieving user with id {}: {}", id, ex.getMessage(), ex))
//                .onErrorResume(ex -> Mono.just(ResponseEntity.internalServerError().build()));
//    }

}
