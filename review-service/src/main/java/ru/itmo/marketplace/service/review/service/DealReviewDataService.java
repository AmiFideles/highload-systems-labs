package ru.itmo.marketplace.service.review.service;

import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.itmo.marketplace.service.review.entity.DealReviewEntity;

public interface DealReviewDataService {

    Mono<DealReviewEntity> createDealReview(DealReviewEntity dealReview);

    Mono<DealReviewEntity> getDealReview(Long dealId);

    Flux<DealReviewEntity> getAllDealReviews(Long userId, Pageable pageable);

    Mono<DealReviewEntity> updateDealReview(DealReviewEntity dealReview);

    Mono<Boolean> deleteDealReview(Long dealId);

}
