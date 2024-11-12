package ru.itmo.marketplace.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.marketplace.dto.DealCreateRequestDto;
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
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    public ResponseEntity<DealResponseDto> createDeal(
            @NotNull @RequestHeader(value = "X-User-Id") Long xUserId,
            @Valid @RequestBody DealCreateRequestDto dealCreateRequestDto
    ) {
        Deal deal = dealCustomMapper.fromDto(dealCreateRequestDto);
        deal = dealService.create(deal, xUserId);
        return ResponseEntity.ok(
                dealCustomMapper.toDto(deal)
        );
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/deals/{id}",
            produces = {"application/json"}
    )
    public ResponseEntity<DealResponseDto> getDealById(
            @PathVariable("id") Long id,
            @NotNull @RequestHeader(value = "X-User-Id") Long xUserId
    ) {
        Deal deal = dealService.findById(id, xUserId).orElseThrow(
                () -> new NotFoundException("Deal with id %s not found".formatted(id))
        );
        return ResponseEntity.ok(
                dealCustomMapper.toDto(deal)
        );
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/deals",
            produces = {"application/json"}
    )
    public ResponseEntity<Page<DealResponseDto>> getDealList(
            @NotNull @RequestHeader(value = "X-User-Id") Long xUserId,
            @Valid @RequestParam(value = "status", required = false) DealStatusDto status,
            Pageable pageable
    ) {
        DealStatus dealStatus = dealCustomMapper.fromDto(status);
        Page<Deal> deals = dealService.findAllByStatus(xUserId, dealStatus, pageable);
        return ResponseEntity.ok(
                deals.map(dealCustomMapper::toDto)
        );
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/deals/{id}",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    public ResponseEntity<DealResponseDto> updateDealStatus(
            @PathVariable("id") Long id,
            @NotNull @RequestHeader(value = "X-User-Id") Long xUserId,
            @Valid @RequestBody DealStatusUpdateRequestDto dealStatusUpdateRequestDto
    ) {
        DealStatusDto status = dealStatusUpdateRequestDto.getStatus();
        DealStatus dealStatus = dealCustomMapper.fromDto(status);
        Deal deal = dealService.update(id, xUserId, dealStatus).orElseThrow(
                () -> new NotFoundException("Deal with id %s not found".formatted(id))
        );
        return ResponseEntity.ok(
                dealCustomMapper.toDto(deal)
        );
    }
}
