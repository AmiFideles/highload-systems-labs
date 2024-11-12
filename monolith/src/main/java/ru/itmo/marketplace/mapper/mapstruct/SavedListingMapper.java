package ru.itmo.marketplace.mapper.mapstruct;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import ru.itmo.marketplace.entity.SavedListing;
import ru.itmo.marketplace.dto.SavedListingPageableResponseDto;
import ru.itmo.marketplace.dto.SavedListingRequestDto;
import ru.itmo.marketplace.dto.SavedListingResponseDto;

@Mapper(
        componentModel = "spring",
        uses = {ListingMapper.class}
)
public interface SavedListingMapper {
    SavedListing fromDto(SavedListingRequestDto savedListingRequestDto);
    SavedListingResponseDto toDto(SavedListing savedListing);
    SavedListingPageableResponseDto toDto(Page<SavedListing> savedListing);
}
