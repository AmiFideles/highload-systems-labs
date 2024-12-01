package ru.itmo.marketplace.service.review.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;
import ru.itmo.common.dto.review.deal.DealReviewRequestDto;
import ru.itmo.common.dto.review.deal.DealReviewResponseDto;

public interface DealReviewService {

    Mono<DealReviewResponseDto> createDealReview(Long authorId, DealReviewRequestDto dealReviewRequestDto);

    Mono<Boolean> deleteDealReview(Long authorId, Long dealId);

    Mono<DealReviewResponseDto> getDealReview(Long authorId, Long dealId);

    Mono<Page<DealReviewResponseDto>> getDealReviews(Long authorId, Pageable pageable);

    Mono<DealReviewResponseDto> updateDealReview(Long authorId, Long dealId, DealReviewRequestDto dealReviewRequestDto);

}
