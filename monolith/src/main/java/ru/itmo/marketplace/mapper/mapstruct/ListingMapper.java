package ru.itmo.marketplace.mapper.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.itmo.marketplace.entity.Listing;
import ru.itmo.marketplace.entity.ListingStatus;
import ru.itmo.marketplace.dto.ListingRequestDto;
import ru.itmo.marketplace.dto.ListingResponseDto;
import ru.itmo.marketplace.dto.ListingStatusDto;
import ru.itmo.marketplace.dto.ModeratedListingResponseDto;

@Mapper(
        componentModel = "spring"
)
public interface ListingMapper {
    Listing fromDto(ListingRequestDto listingRequestDto);
    ListingStatus fromDto(ListingStatusDto listingStatusDto);
    @Mapping(source = "creator.id", target = "creatorId")
    ListingResponseDto toDto(Listing listing);
    ModeratedListingResponseDto toModeratedDto(Listing listing);
}
