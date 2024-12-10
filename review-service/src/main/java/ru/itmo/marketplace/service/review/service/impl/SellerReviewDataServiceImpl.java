package ru.itmo.marketplace.service.review.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.itmo.common.exception.NotFoundException;
import ru.itmo.marketplace.service.review.entity.SellerReviewEntity;
import ru.itmo.marketplace.service.review.repository.SellerReviewRepository;
import ru.itmo.marketplace.service.review.service.SellerReviewDataService;

@Slf4j
@Service
@RequiredArgsConstructor
public class SellerReviewDataServiceImpl implements SellerReviewDataService {
    private final SellerReviewRepository repository;

    @Override
    @Transactional
    public Mono<SellerReviewEntity> create(SellerReviewEntity sellerReviewEntity) {
        return Mono.fromCallable(() -> repository.save(sellerReviewEntity))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    @Transactional
    public Flux<SellerReviewEntity> findByAuthorId(Long id, Pageable pageable) {
        return Flux.defer(() -> Flux.fromIterable(repository.findByAuthorId(id, pageable)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    @Transactional
    public Mono<Boolean> existsByAuthorId(Long authorId, Long sellerId) {
        return Mono.fromCallable(() -> repository.existsByAuthorIdAndSellerId(authorId, sellerId))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    @Transactional
    public Flux<SellerReviewEntity> findAll(Pageable pageable) {
        return Flux.defer(() -> Flux.fromIterable(repository.findAll(pageable)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    @Transactional
    public Mono<SellerReviewEntity> update(SellerReviewEntity sellerReviewEntity) {
        return Mono.fromCallable(() -> {
                    SellerReviewEntity sellerReviewBefore = repository.findByAuthorIdAndSellerId(
                            sellerReviewEntity.getAuthorId(),
                            sellerReviewEntity.getSellerId()
                    );
                    sellerReviewEntity.setCreatedAt(sellerReviewBefore.getCreatedAt());
                    return repository.save(sellerReviewEntity);
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    @Transactional
    public Mono<Boolean> deleteByIds(Long authorId, Long sellerId) {
        return Mono.just(repository.deleteByAuthorIdAndSellerId(authorId, sellerId) > 0);
    }

}
