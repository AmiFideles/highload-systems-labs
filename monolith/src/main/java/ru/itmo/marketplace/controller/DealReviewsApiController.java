package ru.itmo.marketplace.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.marketplace.entity.DealReview;
import ru.itmo.marketplace.mapper.custom.DealReviewCustomMapper;
import ru.itmo.marketplace.dto.DealReviewPageableResponseDto;
import ru.itmo.marketplace.dto.DealReviewRequestDto;
import ru.itmo.marketplace.dto.DealReviewResponseDto;
import ru.itmo.marketplace.service.DealReviewService;
import ru.itmo.marketplace.service.exceptions.NotFoundException;

@Slf4j
@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class DealReviewsApiController {
    private final DealReviewService dealReviewService;
    private final DealReviewCustomMapper dealReviewMapper;

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/deal-reviews",
            produces = { "application/json" },
            consumes = { "application/json" }
    )
    public ResponseEntity<DealReviewResponseDto> createDealReview(Long userId, DealReviewRequestDto dealReviewRequestDto) {
        DealReview dealReview = dealReviewMapper.fromDto(dealReviewRequestDto);

        dealReview = dealReviewService.create(dealReview, userId);
        return ResponseEntity.ok(
                dealReviewMapper.toDto(dealReview)
        );
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/deal-reviews/{id}",
            produces = { "application/json" }
    )
    public ResponseEntity<Void> deleteDealReview(Long id, Long userId) {
        boolean deleted = dealReviewService.deleteById(id, userId);
        if (!deleted) {
            throw new NotFoundException("Deal review with id %s not found".formatted(id));
        }
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/deal-reviews/{id}",
            produces = { "application/json" }
    )
    public ResponseEntity<DealReviewResponseDto> getDealReviewById(Long id) {
        return dealReviewService.findById(id)
                .map(dealReviewMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Deal review with id %s not found".formatted(id)));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/deal-reviews",
            produces = { "application/json" }
    )
    public ResponseEntity<DealReviewPageableResponseDto> getDealReviewList(Long userId, Pageable pageable) {
        Page<DealReview> reviewsPage = dealReviewService.findAll(userId, pageable);
        return ResponseEntity.ok(
                dealReviewMapper.toDto(reviewsPage)
        );
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/deal-reviews/{id}",
            produces = { "application/json" },
            consumes = { "application/json" }
    )
    public ResponseEntity<DealReviewResponseDto> updateDealReview(Long id, Long userId, DealReviewRequestDto dealReviewRequestDto) {
        DealReview dealReview = dealReviewMapper.fromDto(dealReviewRequestDto);
        dealReview.setId(id);

        return dealReviewService.update(dealReview, userId)
                .map(dealReviewMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Deal review with id %s not found".formatted(id)));
    }
}
