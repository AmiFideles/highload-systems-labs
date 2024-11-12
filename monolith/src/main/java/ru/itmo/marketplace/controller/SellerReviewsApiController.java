package ru.itmo.marketplace.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.marketplace.entity.SellerReview;
import ru.itmo.marketplace.mapper.mapstruct.SellerReviewMapper;
import ru.itmo.marketplace.dto.SellerReviewCreateRequestDto;
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
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    public ResponseEntity<SellerReviewResponseDto> createSellerReview(
            @NotNull @RequestHeader(value = "X-User-Id") Long xUserId,
            @Valid @RequestBody SellerReviewCreateRequestDto sellerReviewCreateRequestDto
    ) {
        SellerReview sellerReview = mapper.fromDto(sellerReviewCreateRequestDto);
        sellerReview.setAuthorId(xUserId);

        sellerReview = sellerReviewService.create(sellerReview);

        return ResponseEntity.ok(mapper.toDto(sellerReview));
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/seller-reviews/{seller_id}",
            produces = {"application/json"}
    )
    public ResponseEntity<Void> deleteSellerReview(
            @PathVariable("seller_id") Long sellerId,
            @NotNull @RequestHeader(value = "X-User-Id") Long xUserId
    ) {
        if (!sellerReviewService.deleteById(xUserId, sellerId)) {
            throw new NotFoundException("Seller review with id seller %s not found".formatted(sellerId));
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/seller-reviews",
            produces = {"application/json"}
    )
    public ResponseEntity<Page<SellerReviewResponseDto>> getMySellerReviews(
            @NotNull @RequestHeader(value = "X-User-Id") Long xUserId,
            Pageable pageable
    ) {
        Page<SellerReview> sellerReviews = sellerReviewService.findByAuthorId(xUserId, pageable);
        return ResponseEntity.ok(
                sellerReviews.map(mapper::toDto)
        );
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/seller-reviews/{seller_id}",
            produces = {"application/json"}
    )
    public ResponseEntity<Page<SellerReviewResponseDto>> getSellerReviewsBySellerId(
            @PathVariable("seller_id") Long sellerId,
            Pageable pageable
    ) {
        Page<SellerReview> sellerReviews = sellerReviewService.findAll(sellerId, pageable);
        return ResponseEntity.ok(
                sellerReviews.map(mapper::toDto)
        );
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/seller-reviews/{seller_id}",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    public ResponseEntity<SellerReviewResponseDto> updateSellerReview(
            @PathVariable("seller_id") Long sellerId,
            @NotNull @RequestHeader(value = "X-User-Id") Long xUserId,
            @Valid @RequestBody SellerReviewUpdateRequestDto sellerReviewUpdateRequestDto
    ) {
        SellerReview sellerReview = mapper.fromDto(sellerReviewUpdateRequestDto);
        sellerReview.setAuthorId(xUserId);
        sellerReview.setSellerId(sellerId);

        sellerReview = sellerReviewService.update(sellerReview).orElseThrow(
                () -> new NotFoundException("Seller review with id seller %s not found".formatted(sellerId))
        );

        return ResponseEntity.ok(mapper.toDto(sellerReview));
    }
}
