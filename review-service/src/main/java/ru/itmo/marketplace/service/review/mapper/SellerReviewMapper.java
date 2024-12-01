package ru.itmo.marketplace.service.review.mapper;

import org.mapstruct.Mapper;
import ru.itmo.common.dto.review.seller.SellerReviewCreateRequestDto;
import ru.itmo.common.dto.review.seller.SellerReviewUpdateRequestDto;
import ru.itmo.marketplace.service.review.entity.SellerReviewEntity;

@Mapper(
        componentModel = "spring"
)
public interface SellerReviewMapper {
    SellerReviewEntity toEntity(SellerReviewCreateRequestDto sellerReview);

    SellerReviewEntity toEntity(SellerReviewUpdateRequestDto sellerReview);
}
