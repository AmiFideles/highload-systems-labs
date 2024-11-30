package ru.itmo.marketplace.service.review.controller;

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
import reactor.core.publisher.Mono;
import ru.itmo.common.dto.review.seller.SellerReviewCreateRequestDto;
import ru.itmo.common.dto.review.seller.SellerReviewResponseDto;
import ru.itmo.common.dto.review.seller.SellerReviewUpdateRequestDto;

@Slf4j
@RestController
@RequestMapping("/api/v1/reviews/seller")
@RequiredArgsConstructor
public class SellerReviewsApiController {
    @RequestMapping(
            method = RequestMethod.POST,
            value = "",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    public Mono<ResponseEntity<SellerReviewResponseDto>> createSellerReview(
            @NotNull @RequestHeader(value = "X-User-Id") Long xUserId,
            @Valid @RequestBody SellerReviewCreateRequestDto sellerReviewCreateRequestDto
    ) {
        return Mono.empty();
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/{seller_id}",
            produces = {"application/json"}
    )
    public Mono<ResponseEntity<Void>> deleteSellerReview(
            @PathVariable("seller_id") Long sellerId,
            @NotNull @RequestHeader(value = "X-User-Id") Long xUserId
    ) {
        return Mono.empty();
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "",
            produces = {"application/json"}
    )
    public Mono<ResponseEntity<Page<SellerReviewResponseDto>>> getMySellerReviews(
            @NotNull @RequestHeader(value = "X-User-Id") Long xUserId,
            Pageable pageable
    ) {
        return Mono.empty();
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
        return Mono.empty();
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/{seller_id}",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    public Mono<ResponseEntity<SellerReviewResponseDto>> updateSellerReview(
            @PathVariable("seller_id") Long sellerId,
            @NotNull @RequestHeader(value = "X-User-Id") Long xUserId,
            @Valid @RequestBody SellerReviewUpdateRequestDto sellerReviewUpdateRequestDto
    ) {
        return Mono.empty();
    }
}

