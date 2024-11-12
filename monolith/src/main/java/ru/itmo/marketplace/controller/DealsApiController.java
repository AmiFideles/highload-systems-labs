package ru.itmo.marketplace.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.marketplace.dto.DealCreateRequestDto;
import ru.itmo.marketplace.dto.DealPageableResponseDto;
import ru.itmo.marketplace.dto.DealResponseDto;
import ru.itmo.marketplace.dto.DealStatusDto;
import ru.itmo.marketplace.dto.DealStatusUpdateRequestDto;
import ru.itmo.marketplace.entity.Deal;
import ru.itmo.marketplace.entity.DealStatus;
import ru.itmo.marketplace.mapper.custom.DealCustomMapper;
import ru.itmo.marketplace.service.DealService;
import ru.itmo.marketplace.service.exceptions.NotFoundException;

@Slf4j
@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class DealsApiController {
    private final DealCustomMapper dealCustomMapper;
    private final DealService dealService;

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/deals",
            produces = { "application/json" },
            consumes = { "application/json" }
    )
    public ResponseEntity<DealResponseDto> createDeal(Long userId, DealCreateRequestDto dealCreateRequestDto) {
        Deal deal = dealCustomMapper.fromDto(dealCreateRequestDto);
        deal = dealService.create(deal, userId);
        return ResponseEntity.ok(
                dealCustomMapper.toDto(deal)
        );
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/deals/{id}",
            produces = { "application/json" }
    )
    public ResponseEntity<DealResponseDto> getDealById(Long id, Long userId) {
        Deal deal = dealService.findById(id, userId).orElseThrow(
                () -> new NotFoundException("Deal with id %s not found".formatted(id))
        );
        return ResponseEntity.ok(
                dealCustomMapper.toDto(deal)
        );
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/deals",
            produces = { "application/json" }
    )
    public ResponseEntity<DealPageableResponseDto> getDealList(Long userId, DealStatusDto dealStatusDto, Pageable pageable) {
        DealStatus dealStatus = dealCustomMapper.fromDto(dealStatusDto);
        Page<Deal> deals = dealService.findAllByStatus(userId, dealStatus, pageable);
        return ResponseEntity.ok(
                dealCustomMapper.toDto(deals)
        );
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/deals/{id}",
            produces = { "application/json" },
            consumes = { "application/json" }
    )
    public ResponseEntity<DealResponseDto> updateDealStatus(Long id, Long userId, DealStatusUpdateRequestDto dealStatusUpdateRequestDto) {
        DealStatusDto status = dealStatusUpdateRequestDto.getStatus();
        DealStatus dealStatus = dealCustomMapper.fromDto(status);
        Deal deal = dealService.update(id, userId, dealStatus).orElseThrow(
                () -> new NotFoundException("Deal with id %s not found".formatted(id))
        );
        return ResponseEntity.ok(
                dealCustomMapper.toDto(deal)
        );
    }
}
