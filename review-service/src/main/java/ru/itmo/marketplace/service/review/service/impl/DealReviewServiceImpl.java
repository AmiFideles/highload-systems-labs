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
        // TODO: check if author is deal buyer
        DealReviewEntity entity = dealReviewMapper.toEntity(dealReviewRequestDto);
        return dealReviewDataService.createDealReview(entity)
                .zipWith(dealService.getDeal(entity.getId()))
                .map(t -> {
                    DealReviewEntity dealReview = t.getT1();
                    DealResponseDto deal = t.getT2();
                    return DealReviewResponseDto.builder()
                            .id(dealReview.getId())
                            .comment(dealReview.getComment())
                            .rating(dealReview.getRating())
                            .createdAt(dealReview.getCreatedAt())
                            .deal(deal)
                            .build();
                });
    }

    @Override
    public Mono<Boolean> deleteDealReview(Long authorId, Long dealId) {
        // TODO: check if author is deal buyer
        return dealReviewDataService.deleteDealReview(authorId);
    }

    @Override
    public Mono<DealReviewResponseDto> getDealReview(Long authorId, Long dealId) {
        return dealReviewDataService.getDealReview(authorId)
                .zipWith(dealService.getDeal(dealId))
                .map(t -> {
                    DealReviewEntity dealReview = t.getT1();
                    DealResponseDto deal = t.getT2();
                    return DealReviewResponseDto.builder()
                            .id(dealReview.getId())
                            .comment(dealReview.getComment())
                            .rating(dealReview.getRating())
                            .createdAt(dealReview.getCreatedAt())
                            .deal(deal)
                            .build();
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
        // TODO: check if author is deal buyer
        DealReviewEntity entity = dealReviewMapper.toEntity(dealReviewRequestDto);
        return dealReviewDataService.updateDealReview(entity)
                .zipWith(dealService.getDeal(dealId))
                .map(t -> {
                    DealReviewEntity dealReview = t.getT1();
                    DealResponseDto deal = t.getT2();
                    return DealReviewResponseDto.builder()
                            .id(dealReview.getId())
                            .comment(dealReview.getComment())
                            .rating(dealReview.getRating())
                            .createdAt(dealReview.getCreatedAt())
                            .deal(deal)
                            .build();
                });
    }

}
