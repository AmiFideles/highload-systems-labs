package ru.itmo.marketplace.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.marketplace.api.DealReviewsApi;
import ru.itmo.marketplace.entity.DealReview;
import ru.itmo.marketplace.mapper.custom.DealReviewCustomMapper;
import ru.itmo.marketplace.model.DealReviewPageableResponseDto;
import ru.itmo.marketplace.model.DealReviewRequestDto;
import ru.itmo.marketplace.model.DealReviewResponseDto;
import ru.itmo.marketplace.service.DealReviewService;
import ru.itmo.marketplace.service.exceptions.NotFoundException;

@Slf4j
@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class DealReviewsApiController implements DealReviewsApi {
    private final DealReviewService dealReviewService;
    private final DealReviewCustomMapper dealReviewMapper;

    @Override
    public ResponseEntity<DealReviewResponseDto> createDealReview(Long userId, DealReviewRequestDto dealReviewRequestDto) {
        DealReview dealReview = dealReviewMapper.fromDto(dealReviewRequestDto);

        dealReview = dealReviewService.create(dealReview, userId);
        return ResponseEntity.ok(
                dealReviewMapper.toDto(dealReview)
        );
    }

    @Override
    public ResponseEntity<Void> deleteDealReview(Long id, Long userId) {
        boolean deleted = dealReviewService.deleteById(id, userId);
        if (!deleted) {
            throw new NotFoundException("Deal review with id %s not found".formatted(id));
        }
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<DealReviewResponseDto> getDealReviewById(Long id) {
        return dealReviewService.findById(id)
                .map(dealReviewMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Deal review with id %s not found".formatted(id)));
    }

    @Override
    public ResponseEntity<DealReviewPageableResponseDto> getDealReviewList(Long userId, Pageable pageable) {
        Page<DealReview> reviewsPage = dealReviewService.findAll(userId, pageable);
        return ResponseEntity.ok(
                dealReviewMapper.toDto(reviewsPage)
        );
    }

    @Override
    public ResponseEntity<DealReviewResponseDto> updateDealReview(Long id, Long userId, DealReviewRequestDto dealReviewRequestDto) {
        DealReview dealReview = dealReviewMapper.fromDto(dealReviewRequestDto);
        dealReview.setId(id);

        return dealReviewService.update(dealReview, userId)
                .map(dealReviewMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Deal review with id %s not found".formatted(id)));
    }
}
