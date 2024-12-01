package ru.itmo.service.market.mapper.mapstruct;

import org.mapstruct.Mapper;
import ru.itmo.common.dto.listing.ListingRequestDto;
import ru.itmo.common.dto.listing.ListingResponseDto;
import ru.itmo.common.dto.listing.ListingStatusDto;
import ru.itmo.common.dto.listing.ModeratedListingResponseDto;
import ru.itmo.service.market.entity.Listing;
import ru.itmo.service.market.entity.ListingStatus;


@Mapper(
        componentModel = "spring"
)
public interface ListingMapper {
    Listing fromDto(ListingRequestDto listingRequestDto);
    ListingStatus fromDto(ListingStatusDto listingStatusDto);
    ListingResponseDto toDto(Listing listing);
    ModeratedListingResponseDto toModeratedDto(Listing listing);
}
