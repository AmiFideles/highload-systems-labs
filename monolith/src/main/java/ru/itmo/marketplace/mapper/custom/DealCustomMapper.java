package ru.itmo.marketplace.mapper.custom;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.itmo.marketplace.entity.Deal;
import ru.itmo.marketplace.entity.DealStatus;
import ru.itmo.marketplace.entity.Listing;
import ru.itmo.marketplace.mapper.mapstruct.DealMapper;
import ru.itmo.marketplace.dto.DealCreateRequestDto;
import ru.itmo.marketplace.dto.DealResponseDto;
import ru.itmo.marketplace.dto.DealStatusDto;
import ru.itmo.marketplace.repository.ListingRepository;
import ru.itmo.marketplace.service.exceptions.NotFoundException;

@Component
@RequiredArgsConstructor
public class DealCustomMapper {
    private final DealMapper dealMapper;
    private final ListingCustomMapper listingCustomMapper;
    private final ListingRepository listingRepository;


    public Deal fromDto(DealCreateRequestDto dealRequestDto) {
        Deal deal = dealMapper.fromDto(dealRequestDto);

        Listing listing = listingRepository.findById(dealRequestDto.getListingId())
                .orElseThrow(() -> new NotFoundException("Listing not found with id: " + dealRequestDto.getListingId()));
        deal.setListing(listing);

        return deal;
    }

    public DealResponseDto toDto(Deal deal) {
        DealResponseDto dto = dealMapper.toDto(deal);
        dto.setListing(listingCustomMapper.toDto(deal.getListing()));
        return dto;
    }

    public DealStatusDto toDto(DealStatus status) {
        return dealMapper.toDto(status);
    }

    public DealStatus fromDto(DealStatusDto dealStatusDto) {
        return dealMapper.fromDto(dealStatusDto);
    }
}
