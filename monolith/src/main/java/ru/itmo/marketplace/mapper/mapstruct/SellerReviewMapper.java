package ru.itmo.marketplace.mapper.mapstruct;

import org.mapstruct.Mapper;
import ru.itmo.marketplace.entity.SellerReview;
import ru.itmo.marketplace.dto.SellerReviewCreateRequestDto;
import ru.itmo.marketplace.dto.SellerReviewResponseDto;
import ru.itmo.marketplace.dto.SellerReviewUpdateRequestDto;

@Mapper(
        componentModel = "spring",
        uses = UserMapper.class
)
public interface SellerReviewMapper {
    SellerReview fromDto(SellerReviewCreateRequestDto sellerReviewRequestDto);
    SellerReview fromDto(SellerReviewUpdateRequestDto sellerReviewRequestDto);
    SellerReviewResponseDto toDto(SellerReview sellerReview);
}
