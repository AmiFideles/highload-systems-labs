package ru.itmo.marketplace.mapper.mapstruct;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import ru.itmo.marketplace.entity.SavedListing;
import ru.itmo.marketplace.model.SavedListingPageableResponseDto;
import ru.itmo.marketplace.model.SavedListingRequestDto;
import ru.itmo.marketplace.model.SavedListingResponseDto;

@Mapper(
        componentModel = "spring",
        uses = {ListingMapper.class}
)
public interface SavedListingMapper {
    SavedListing fromDto(SavedListingRequestDto savedListingRequestDto);
    SavedListingResponseDto toDto(SavedListing savedListing);
    SavedListingPageableResponseDto toDto(Page<SavedListing> savedListing);
}
