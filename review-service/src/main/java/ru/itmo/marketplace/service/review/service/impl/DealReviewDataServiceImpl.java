package ru.itmo.marketplace.service.review.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.itmo.marketplace.service.review.entity.DealReviewEntity;
import ru.itmo.marketplace.service.review.repository.DealReviewRepository;
import ru.itmo.marketplace.service.review.service.DealReviewDataService;

@Slf4j
@Service
@RequiredArgsConstructor
public class DealReviewDataServiceImpl implements DealReviewDataService {
    private final DealReviewRepository dealReviewRepository;

    @Override
    public Mono<DealReviewEntity> createDealReview(DealReviewEntity dealReview) {
        return Mono.just(dealReviewRepository.save(dealReview));
    }

    @Override
    public Mono<DealReviewEntity> getDealReview(Long dealId) {
        return Mono.justOrEmpty(dealReviewRepository.findById(dealId));
    }

    @Override
    public Flux<DealReviewEntity> getAllDealReviews(Long userId, Pageable pageable) {
        return Flux.fromIterable(dealReviewRepository.findAllByBuyerId(userId, pageable));
    }

    @Override
    public Mono<DealReviewEntity> updateDealReview(DealReviewEntity dealReview) {
        return Mono.just(dealReviewRepository.save(dealReview));
    }

    @Override
    public Mono<Boolean> deleteDealReview(Long dealId) {
        return Mono.just(dealReviewRepository.removeById(dealId) > 0);
    }

}
