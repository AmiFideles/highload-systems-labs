package ru.itmo.service.market.mapper.custom;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.itmo.common.dto.saved.SavedListingRequestDto;
import ru.itmo.common.dto.saved.SavedListingResponseDto;
import ru.itmo.service.market.entity.SavedListing;
import ru.itmo.service.market.mapper.mapstruct.SavedListingMapper;


@Component
@RequiredArgsConstructor
public class SavedListingCustomMapper {
    private final SavedListingMapper savedListingMapper;
    private final ListingCustomMapper listingCustomMapper;

    public SavedListing fromDto(SavedListingRequestDto savedListingRequestDto){
        return savedListingMapper.fromDto(savedListingRequestDto);
    }

    public SavedListingResponseDto toDto(SavedListing savedListing){
        SavedListingResponseDto dto = savedListingMapper.toDto(savedListing);
        dto.setListing(listingCustomMapper.toDto(savedListing.getListing()));
        return dto;
    }
}
