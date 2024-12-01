package ru.itmo.service.market.mapper.mapstruct;

import org.mapstruct.Mapper;
import ru.itmo.common.dto.deal.DealCreateRequestDto;
import ru.itmo.common.dto.deal.DealResponseDto;
import ru.itmo.common.dto.deal.DealStatusDto;
import ru.itmo.service.market.entity.Deal;
import ru.itmo.service.market.entity.DealStatus;


@Mapper(
        componentModel = "spring",
        uses = {ListingMapper.class}
)
public interface DealMapper {
    Deal fromDto(DealCreateRequestDto dealRequestDto);

    DealResponseDto toDto(Deal deal);

    DealStatusDto toDto(DealStatus status);

    DealStatus fromDto(DealStatusDto dealStatusDto);
}
