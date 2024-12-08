package ru.itmo.marketplace.mapper.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.itmo.marketplace.entity.Deal;
import ru.itmo.marketplace.entity.DealStatus;
import ru.itmo.marketplace.dto.DealCreateRequestDto;
import ru.itmo.marketplace.dto.DealResponseDto;
import ru.itmo.marketplace.dto.DealStatusDto;

@Mapper(
        componentModel = "spring",
        uses = {ListingMapper.class}
)
public interface DealMapper {
    Deal fromDto(DealCreateRequestDto dealRequestDto);

    @Mapping(source = "buyer.id", target = "buyerId")
    DealResponseDto toDto(Deal deal);

    DealStatusDto toDto(DealStatus status);

    DealStatus fromDto(DealStatusDto dealStatusDto);
}
