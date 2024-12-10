package ru.itmo.service.market.mapper.custom;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.itmo.common.dto.deal.DealCreateRequestDto;
import ru.itmo.common.dto.deal.DealResponseDto;
import ru.itmo.common.dto.deal.DealStatusDto;
import ru.itmo.common.dto.user.UserResponseDto;
import ru.itmo.common.exception.NotFoundException;
import ru.itmo.modules.security.UserSecurityContextHolder;
import ru.itmo.service.market.entity.Deal;
import ru.itmo.service.market.entity.DealStatus;
import ru.itmo.service.market.entity.Listing;
import ru.itmo.service.market.mapper.mapstruct.DealMapper;
import ru.itmo.service.market.repository.ListingRepository;
import ru.itmo.service.user.client.UserApiClient;


@Component
@RequiredArgsConstructor
public class DealCustomMapper {
    private final DealMapper dealMapper;
    private final ListingCustomMapper listingCustomMapper;
    private final ListingRepository listingRepository;
    private final UserApiClient userApiClient;
    private final UserSecurityContextHolder contextHolder;

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
        UserResponseDto buyer = userApiClient.getUserById(
                deal.getBuyerId(),
                contextHolder.getUserId(),
                contextHolder.getUserRole()
        );
        dto.setBuyer(buyer);
        return dto;
    }

    public DealStatusDto toDto(DealStatus status) {
        return dealMapper.toDto(status);
    }

    public DealStatus fromDto(DealStatusDto dealStatusDto) {
        return dealMapper.fromDto(dealStatusDto);
    }
}
