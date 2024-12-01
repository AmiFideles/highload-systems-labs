package ru.itmo.marketplace.service.review.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.itmo.common.dto.review.deal.DealReviewRequestDto;
import ru.itmo.common.dto.review.deal.DealReviewResponseDto;

@Slf4j
@RestController
@RequestMapping("/api/v1/reviews/deal")
@RequiredArgsConstructor
public class DealReviewsApiController {
    @RequestMapping(
            method = RequestMethod.POST,
            value = "",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    public Mono<ResponseEntity<DealReviewResponseDto>> createDealReview(
            @Valid @RequestBody DealReviewRequestDto dealReviewRequestDto
    ) {
        return Mono.empty();
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/{id}",
            produces = {"application/json"}
    )
    public Mono<ResponseEntity<Void>> deleteDealReview(
            @PathVariable("id") Long id
    ) {
        return Mono.empty();
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/{id}",
            produces = {"application/json"}
    )
    public Mono<ResponseEntity<DealReviewResponseDto>> getDealReviewById(
            @PathVariable("id") Long id
    ) {
        return Mono.empty();
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "",
            produces = {"application/json"}
    )
    public Mono<ResponseEntity<Page<DealReviewResponseDto>>> getDealReviewList(
            Pageable pageable
    ) {
        return Mono.empty();
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/{id}",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    public Mono<ResponseEntity<DealReviewResponseDto>> updateDealReview(
            @PathVariable("id") Long id,
            @Valid @RequestBody DealReviewRequestDto dealReviewRequestDto
    ) {
        return Mono.empty();
    }
}
