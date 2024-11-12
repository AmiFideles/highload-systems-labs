package ru.itmo.marketplace.mapper.mapstruct;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import ru.itmo.marketplace.entity.DealReview;
import ru.itmo.marketplace.dto.DealReviewPageableResponseDto;
import ru.itmo.marketplace.dto.DealReviewRequestDto;
import ru.itmo.marketplace.dto.DealReviewResponseDto;

@Mapper(
        componentModel = "spring"
)
public interface DealReviewMapper {
    DealReview fromDto(DealReviewRequestDto dealReviewRequestDto);
    DealReviewResponseDto toDto(DealReview dealReview);
    DealReviewPageableResponseDto toDto(Page<DealReview> dealReview);
}
