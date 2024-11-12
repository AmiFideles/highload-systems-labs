package ru.itmo.marketplace.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.marketplace.entity.SellerReview;
import ru.itmo.marketplace.mapper.mapstruct.SellerReviewMapper;
import ru.itmo.marketplace.dto.SellerReviewCreateRequestDto;
import ru.itmo.marketplace.dto.SellerReviewPageableResponseDto;
import ru.itmo.marketplace.dto.SellerReviewResponseDto;
import ru.itmo.marketplace.dto.SellerReviewUpdateRequestDto;
import ru.itmo.marketplace.service.SellerReviewService;
import ru.itmo.marketplace.service.exceptions.NotFoundException;

@Slf4j
@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class SellerReviewsApiController {
    private final SellerReviewService sellerReviewService;
    private final SellerReviewMapper mapper;


    @RequestMapping(
            method = RequestMethod.POST,
            value = "/seller-reviews",
            produces = { "application/json" },
            consumes = { "application/json" }
    )
    public ResponseEntity<SellerReviewResponseDto> createSellerReview(
            Long userId, SellerReviewCreateRequestDto sellerReviewCreateRequestDto
    ) {
        SellerReview sellerReview = mapper.fromDto(sellerReviewCreateRequestDto);
        sellerReview.setAuthorId(userId);

        sellerReview = sellerReviewService.create(sellerReview);

        return ResponseEntity.ok(mapper.toDto(sellerReview));
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/seller-reviews/{seller_id}",
            produces = { "application/json" }
    )
    public ResponseEntity<Void> deleteSellerReview(Long sellerId, Long userId) {
        if (!sellerReviewService.deleteById(userId, sellerId)) {
            throw new NotFoundException("Seller review with id seller %s not found".formatted(sellerId));
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/seller-reviews",
            produces = { "application/json" }
    )
    public ResponseEntity<SellerReviewPageableResponseDto> getMySellerReviews(Long userId, Pageable pageable) {
        Page<SellerReview> sellerReviews = sellerReviewService.findByAuthorId(userId, pageable);
        return ResponseEntity.ok(mapper.toDto(sellerReviews));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/seller-reviews/{seller_id}",
            produces = { "application/json" }
    )
    public ResponseEntity<SellerReviewPageableResponseDto> getSellerReviewsBySellerId(
            Long sellerId, Pageable pageable
    ) {
        Page<SellerReview> sellerReviews = sellerReviewService.findAll(sellerId, pageable);
        return ResponseEntity.ok(mapper.toDto(sellerReviews));
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/seller-reviews/{seller_id}",
            produces = { "application/json" },
            consumes = { "application/json" }
    )
    public ResponseEntity<SellerReviewResponseDto> updateSellerReview(
            Long sellerId, Long userId, SellerReviewUpdateRequestDto sellerReviewUpdateRequestDto
    ) {
        SellerReview sellerReview = mapper.fromDto(sellerReviewUpdateRequestDto);
        sellerReview.setAuthorId(userId);
        sellerReview.setSellerId(sellerId);

        sellerReview = sellerReviewService.update(sellerReview).orElseThrow(
                () -> new NotFoundException("Seller review with id seller %s not found".formatted(sellerId))
        );

        return ResponseEntity.ok(mapper.toDto(sellerReview));
    }
}
