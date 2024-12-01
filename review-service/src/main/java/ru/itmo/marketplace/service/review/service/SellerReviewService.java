package ru.itmo.marketplace.service.review.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;
import ru.itmo.common.dto.review.seller.SellerReviewCreateRequestDto;
import ru.itmo.common.dto.review.seller.SellerReviewResponseDto;
import ru.itmo.common.dto.review.seller.SellerReviewUpdateRequestDto;

public interface SellerReviewService {
    Mono<SellerReviewResponseDto> create(Long authorId, SellerReviewCreateRequestDto createRequestDto);

    Mono<Page<SellerReviewResponseDto>> findByAuthorId(Long id, Pageable pageable);

    Mono<Page<SellerReviewResponseDto>> findAll(Pageable pageable);

    Mono<SellerReviewResponseDto> update(Long authorId, Long sellerId, SellerReviewUpdateRequestDto updateRequestDto);

    Mono<Boolean> deleteByIds(Long authorId, Long sellerId);
}
