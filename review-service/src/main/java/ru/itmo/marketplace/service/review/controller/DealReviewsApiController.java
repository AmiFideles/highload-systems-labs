package ru.itmo.marketplace.service.review.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.itmo.common.dto.review.deal.DealReviewRequestDto;
import ru.itmo.common.dto.review.deal.DealReviewResponseDto;
import ru.itmo.marketplace.service.review.service.DealReviewService;
import ru.itmo.modules.security.InternalAuthentication;

@Slf4j
@RestController
@RequestMapping("/api/v1/reviews/deals")
@RequiredArgsConstructor
public class DealReviewsApiController {
    private final DealReviewService dealReviewService;

    @RequestMapping(
            method = RequestMethod.POST,
            value = "",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    public Mono<ResponseEntity<DealReviewResponseDto>> createDealReview(
            @AuthenticationPrincipal InternalAuthentication authentication,
            @Valid @RequestBody DealReviewRequestDto dealReviewRequestDto
    ) {
        return dealReviewService.createDealReview(authentication.getUserId(), dealReviewRequestDto)
                .map(ResponseEntity::ok);
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/{id}",
            produces = {"application/json"}
    )
    public Mono<ResponseEntity<Void>> deleteDealReview(
            @AuthenticationPrincipal InternalAuthentication authentication,
            @PathVariable("id") Long id
    ) {
        return dealReviewService.deleteDealReview(authentication.getUserId(), id)
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/{id}",
            produces = {"application/json"}
    )
    public Mono<ResponseEntity<DealReviewResponseDto>> getDealReviewById(
            @AuthenticationPrincipal InternalAuthentication authentication,
            @PathVariable("id") Long id
    ) {
        return dealReviewService.getDealReview(authentication.getUserId(), id)
                .map(ResponseEntity::ok);
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "",
            produces = {"application/json"}
    )
    public Mono<ResponseEntity<Page<DealReviewResponseDto>>> getDealReviewList(
            @AuthenticationPrincipal InternalAuthentication authentication,
            Pageable pageable
    ) {
        return dealReviewService.getDealReviews(authentication.getUserId(), pageable)
                .map(ResponseEntity::ok);
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/{id}",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    public Mono<ResponseEntity<DealReviewResponseDto>> updateDealReview(
            @AuthenticationPrincipal InternalAuthentication authentication,
            @PathVariable("id") Long id,
            @Valid @RequestBody DealReviewRequestDto dealReviewRequestDto
    ) {
        return dealReviewService.updateDealReview(authentication.getUserId(), id, dealReviewRequestDto)
                .map(ResponseEntity::ok);
    }

}
