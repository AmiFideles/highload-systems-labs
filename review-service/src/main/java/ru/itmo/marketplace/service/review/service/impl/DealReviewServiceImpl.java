package ru.itmo.marketplace.service.review.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.itmo.common.dto.deal.DealResponseDto;
import ru.itmo.common.dto.review.deal.DealReviewRequestDto;
import ru.itmo.common.dto.review.deal.DealReviewResponseDto;
import ru.itmo.common.exception.DuplicateException;
import ru.itmo.common.exception.NotFoundException;
import ru.itmo.marketplace.service.review.entity.DealReviewEntity;
import ru.itmo.marketplace.service.review.mapper.DealReviewMapper;
import ru.itmo.marketplace.service.review.service.DealReviewDataService;
import ru.itmo.marketplace.service.review.service.DealReviewService;
import ru.itmo.marketplace.service.review.service.DealService;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DealReviewServiceImpl implements DealReviewService {
    private final DealReviewMapper dealReviewMapper;
    private final DealReviewDataService dealReviewDataService;
    private final DealService dealService;

    @Override
    public Mono<DealReviewResponseDto> createDealReview(Long authorId, DealReviewRequestDto dealReviewRequestDto) {
        DealReviewEntity entity = dealReviewMapper.toEntity(dealReviewRequestDto);
        Mono<DealResponseDto> dealMono = dealService.getDeal(entity.getId()).switchIfEmpty(
                Mono.error(() -> new NotFoundException("Deal with id=%s not found".formatted(entity.getId())))
        );
        return dealReviewDataService.existsDealReview(entity.getId()).flatMap(exists -> {
            if (exists) {
                return Mono.error(new DuplicateException("Deal with id=%s already exists".formatted(entity.getId())));
            }
            return dealMono.flatMap(deal ->
                    dealReviewDataService.createDealReview(entity).map(dealReview ->
                            DealReviewResponseDto.builder()
                                    .id(dealReview.getId())
                                    .comment(dealReview.getComment())
                                    .rating(dealReview.getRating())
                                    .createdAt(dealReview.getCreatedAt())
                                    .deal(deal)
                                    .build()
                    )
            );
        });
    }

    @Override
    public Mono<Boolean> deleteDealReview(Long authorId, Long dealId) {
        return dealReviewDataService.existsDealReview(dealId).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new NotFoundException("Deal review with id=%s not found".formatted(dealId)));
            }
            return dealReviewDataService.deleteDealReview(authorId);
        });
    }

    @Override
    public Mono<DealReviewResponseDto> getDealReview(Long authorId, Long dealId) {
        Mono<DealResponseDto> dealMono = dealService.getDeal(dealId).switchIfEmpty(
                Mono.error(() -> new NotFoundException("Deal with id=%s not found".formatted(dealId)))
        );
        return dealReviewDataService.existsDealReview(dealId).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new NotFoundException("Deal review with id=%s not found".formatted(dealId)));
            }
            return dealMono.flatMap(deal ->
                    dealReviewDataService.getDealReview(authorId).map(dealReview ->
                            DealReviewResponseDto.builder()
                                    .id(dealReview.getId())
                                    .comment(dealReview.getComment())
                                    .rating(dealReview.getRating())
                                    .createdAt(dealReview.getCreatedAt())
                                    .deal(deal)
                                    .build()));
        });
    }

    @Override
    public Mono<Page<DealReviewResponseDto>> getDealReviews(Long authorId, Pageable pageable) {
        return dealReviewDataService.getAllDealReviews(authorId, pageable)
                .collectList()
                .flatMap(dealReviewEntities -> {
                    List<Long> dealsIds = dealReviewEntities.stream().map(DealReviewEntity::getId).toList();
                    return dealService.getAllDealsByIds(dealsIds)
                            .collectMap(DealResponseDto::getId)
                            .map(deals ->
                                    dealReviewEntities.stream()
                                            .map(dealReview -> DealReviewResponseDto.builder()
                                                    .id(dealReview.getId())
                                                    .rating(dealReview.getRating())
                                                    .comment(dealReview.getComment())
                                                    .deal(deals.get(dealReview.getId()))
                                                    .createdAt(dealReview.getCreatedAt())
                                                    .build())
                                            .toList()
                            );
                }).map(sellerReviews ->
                        new PageImpl<>(sellerReviews, pageable, sellerReviews.size())
                );
    }

    @Override
    public Mono<DealReviewResponseDto> updateDealReview(
            Long authorId,
            Long dealId,
            DealReviewRequestDto dealReviewRequestDto
    ) {
        DealReviewEntity entity = dealReviewMapper.toEntity(dealReviewRequestDto);
        Mono<DealResponseDto> dealMono = dealService.getDeal(dealId).switchIfEmpty(
                Mono.error(() -> new NotFoundException("Deal with id=%s not found".formatted(dealId)))
        );
        return dealReviewDataService.existsDealReview(dealId).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new NotFoundException("Deal review with id=%s not found".formatted(dealId)));
            }
            return dealMono.flatMap(deal ->
                    dealReviewDataService.updateDealReview(entity).map(dealReview ->
                            DealReviewResponseDto.builder()
                                    .id(dealReview.getId())
                                    .comment(dealReview.getComment())
                                    .rating(dealReview.getRating())
                                    .createdAt(dealReview.getCreatedAt())
                                    .deal(deal)
                                    .build()));
        });
    }

}
