package ru.itmo.marketplace.mapper.custom;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import ru.itmo.marketplace.entity.SavedListing;
import ru.itmo.marketplace.mapper.mapstruct.SavedListingMapper;
import ru.itmo.marketplace.dto.SavedListingPageableResponseDto;
import ru.itmo.marketplace.dto.SavedListingRequestDto;
import ru.itmo.marketplace.dto.SavedListingResponseDto;

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

    public SavedListingPageableResponseDto toDto(Page<SavedListing> savedListing){
        SavedListingPageableResponseDto dto = savedListingMapper.toDto(savedListing);
        dto.setContent(savedListing.getContent().stream().map(this::toDto).toList());
        return dto;
    }
}
