package ru.itmo.marketplace.mapper.mapstruct;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import ru.itmo.marketplace.entity.SellerReview;
import ru.itmo.marketplace.model.SellerReviewCreateRequestDto;
import ru.itmo.marketplace.model.SellerReviewPageableResponseDto;
import ru.itmo.marketplace.model.SellerReviewResponseDto;
import ru.itmo.marketplace.model.SellerReviewUpdateRequestDto;

@Mapper(
        componentModel = "spring",
        uses = UserMapper.class
)
public interface SellerReviewMapper {
    SellerReview fromDto(SellerReviewCreateRequestDto sellerReviewRequestDto);
    SellerReview fromDto(SellerReviewUpdateRequestDto sellerReviewRequestDto);
    SellerReviewResponseDto toDto(SellerReview sellerReview);
    SellerReviewPageableResponseDto toDto(Page<SellerReview> sellerReview);
}
