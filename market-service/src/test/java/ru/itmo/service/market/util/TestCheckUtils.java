package ru.itmo.service.market.util;

import lombok.experimental.UtilityClass;
import ru.itmo.common.dto.deal.DealResponseDto;
import ru.itmo.common.dto.listing.ListingResponseDto;
import ru.itmo.service.market.entity.Deal;
import ru.itmo.service.market.entity.Listing;

import static org.assertj.core.api.Assertions.assertThat;

@UtilityClass
public class TestCheckUtils {
    public static final long UNKNOWN_ID = 9999999L;

    public static void checkListing(ListingResponseDto listingDto, Listing listing) {
        assertThat(listingDto.getId()).isEqualTo(listing.getId());
        assertThat(listingDto.getUsed()).isEqualTo(listing.isUsed());
        assertThat(listingDto.getDescription()).isEqualTo(listing.getDescription());
        assertThat(listingDto.getCreator().getId()).isEqualTo(listing.getCreatorId());
        assertThat(listingDto.getPrice()).isEqualTo(listing.getPrice());
        assertThat(listingDto.getCategories().size()).isEqualTo(listing.getCategories().size());
    }

    public void checkDeal(DealResponseDto dealDto, Deal deal) {
        assertThat(dealDto.getId()).isEqualTo(deal.getId());
        assertThat(dealDto.getTotalPrice()).isEqualTo(deal.getTotalPrice());
        assertThat(dealDto.getListing().getId()).isEqualTo(deal.getListing().getId());
        assertThat(dealDto.getBuyer().getId()).isEqualTo(deal.getBuyerId());
        assertThat(dealDto.getCreatedAt()).isEqualTo(deal.getCreatedAt());
        assertThat(dealDto.getStatus().toString()).isEqualTo(deal.getStatus().toString());
    }
}
