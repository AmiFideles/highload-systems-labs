package ru.itmo.marketplace.mapper.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import ru.itmo.marketplace.entity.Deal;
import ru.itmo.marketplace.entity.DealStatus;
import ru.itmo.marketplace.model.DealCreateRequestDto;
import ru.itmo.marketplace.model.DealPageableResponseDto;
import ru.itmo.marketplace.model.DealResponseDto;
import ru.itmo.marketplace.model.DealStatusDto;

@Mapper(
        componentModel = "spring",
        uses = {ListingMapper.class}
)
public interface DealMapper {
    Deal fromDto(DealCreateRequestDto dealRequestDto);

    @Mapping(source = "buyer.id", target = "buyerId")
    DealResponseDto toDto(Deal deal);

    DealPageableResponseDto toDto(Page<Deal> deal);

    DealStatusDto toDto(DealStatus status);

    DealStatus fromDto(DealStatusDto dealStatusDto);
}
