package ru.itmo.marketplace.mapper.custom;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.itmo.marketplace.entity.Deal;
import ru.itmo.marketplace.entity.DealReview;
import ru.itmo.marketplace.mapper.mapstruct.DealReviewMapper;
import ru.itmo.marketplace.dto.DealReviewRequestDto;
import ru.itmo.marketplace.dto.DealReviewResponseDto;
import ru.itmo.marketplace.repository.DealRepository;
import ru.itmo.marketplace.service.exceptions.NotFoundException;

@Component
@RequiredArgsConstructor
public class DealReviewCustomMapper {
    private final DealReviewMapper dealReviewMapper;
    private final DealCustomMapper dealCustomMapper;
    private final DealRepository dealRepository;

    public DealReview fromDto(DealReviewRequestDto dealReviewRequestDto) {
        Deal deal = dealRepository.findById(dealReviewRequestDto.getDealId())
                .orElseThrow(() -> new NotFoundException("Deal not found with id: " + dealReviewRequestDto.getDealId()));

        DealReview dealReview = dealReviewMapper.fromDto(dealReviewRequestDto);
        dealReview.setDeal(deal);

        return dealReview;
    }

    public DealReviewResponseDto toDto(DealReview dealReview) {
        DealReviewResponseDto dto = dealReviewMapper.toDto(dealReview);
        dto.setDeal(dealCustomMapper.toDto(dealReview.getDeal()));
        return dto;
    }
}
