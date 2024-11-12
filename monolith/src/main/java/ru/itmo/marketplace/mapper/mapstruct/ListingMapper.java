package ru.itmo.marketplace.mapper.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import ru.itmo.marketplace.entity.Listing;
import ru.itmo.marketplace.entity.ListingStatus;
import ru.itmo.marketplace.dto.ListingPageableResponseDto;
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
    ListingPageableResponseDto toDto(Page<Listing> listing);
    ModeratedListingResponseDto toModeratedDto(Listing listing);
}
