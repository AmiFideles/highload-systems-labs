package ru.itmo.service.market.mapper.mapstruct;

import org.mapstruct.Mapper;
import ru.itmo.common.dto.saved.SavedListingRequestDto;
import ru.itmo.common.dto.saved.SavedListingResponseDto;
import ru.itmo.service.market.entity.SavedListing;


@Mapper(
        componentModel = "spring",
        uses = {ListingMapper.class}
)
public interface SavedListingMapper {
    SavedListing fromDto(SavedListingRequestDto savedListingRequestDto);
    SavedListingResponseDto toDto(SavedListing savedListing);
}
