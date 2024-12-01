package ru.itmo.marketplace.service.review.service;

import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.itmo.marketplace.service.review.entity.SellerReviewEntity;

public interface SellerReviewDataService {
    Mono<SellerReviewEntity> create(SellerReviewEntity sellerReviewEntity);

    Flux<SellerReviewEntity> findByAuthorId(Long id, Pageable pageable);

    Flux<SellerReviewEntity> findAll(Pageable pageable);

    Mono<SellerReviewEntity> update(SellerReviewEntity sellerReviewEntity);

    Mono<Boolean> deleteByIds(Long authorId, Long sellerId);
}
