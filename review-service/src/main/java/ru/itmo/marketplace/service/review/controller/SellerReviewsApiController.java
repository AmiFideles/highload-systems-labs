package ru.itmo.marketplace.service.review.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.itmo.common.dto.review.seller.SellerReviewCreateRequestDto;
import ru.itmo.common.dto.review.seller.SellerReviewResponseDto;
import ru.itmo.common.dto.review.seller.SellerReviewUpdateRequestDto;
import ru.itmo.marketplace.service.review.service.SellerReviewService;
import ru.itmo.modules.security.InternalAuthentication;

@Slf4j
@RestController
@RequestMapping("/api/v1/reviews/sellers")
@RequiredArgsConstructor
public class SellerReviewsApiController {
    private final SellerReviewService sellerReviewService;

    @RequestMapping(
            method = RequestMethod.POST,
            value = "",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    @PreAuthorize("hasAuthority('BUYER')")
    public Mono<ResponseEntity<SellerReviewResponseDto>> createSellerReview(
            @AuthenticationPrincipal InternalAuthentication authentication,
            @Valid @RequestBody SellerReviewCreateRequestDto sellerReviewCreateRequestDto
    ) {
        return sellerReviewService.create(authentication.getUserId(), sellerReviewCreateRequestDto)
                .map(ResponseEntity::ok);
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/{seller_id}",
            produces = {"application/json"}
    )
    @PreAuthorize("hasAuthority('BUYER') or hasAuthority('ADMIN')")
    public Mono<ResponseEntity<Void>> deleteSellerReview(
            @AuthenticationPrincipal InternalAuthentication authentication,
            @PathVariable("seller_id") Long sellerId
    ) {
        return sellerReviewService.deleteByIds(authentication.getUserId(), sellerId)
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "",
            produces = {"application/json"}
    )
    @PreAuthorize("hasAuthority('BUYER')")
    public Mono<ResponseEntity<Page<SellerReviewResponseDto>>> getMySellerReviews(
            @AuthenticationPrincipal InternalAuthentication authentication,
            Pageable pageable
    ) {
        return sellerReviewService.findByAuthorId(authentication.getUserId(), pageable)
                .map(ResponseEntity::ok);
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/{seller_id}",
            produces = {"application/json"}
    )
    public Mono<ResponseEntity<Page<SellerReviewResponseDto>>> getSellerReviewsBySellerId(
            @PathVariable("seller_id") Long sellerId,
            Pageable pageable
    ) {
        return sellerReviewService.findByAuthorId(sellerId, pageable)
                .map(ResponseEntity::ok);
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/{seller_id}",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    @PreAuthorize("hasAuthority('BUYER')")
    public Mono<ResponseEntity<SellerReviewResponseDto>> updateSellerReview(
            @AuthenticationPrincipal InternalAuthentication authentication,
            @PathVariable("seller_id") Long sellerId,
            @Valid @RequestBody SellerReviewUpdateRequestDto sellerReviewUpdateRequestDto
    ) {
        return sellerReviewService.update(authentication.getUserId(), sellerId, sellerReviewUpdateRequestDto)
                .map(ResponseEntity::ok);
    }
}

