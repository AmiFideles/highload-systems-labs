package ru.itmo.marketplace.service.review.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.itmo.common.dto.review.deal.DealReviewRequestDto;
import ru.itmo.marketplace.service.review.entity.DealReviewEntity;

@Mapper(
        componentModel = "spring"
)
public interface DealReviewMapper {

    @Mapping(target = "id", source = "dealId")
    DealReviewEntity toEntity(DealReviewRequestDto dealReview);

}
