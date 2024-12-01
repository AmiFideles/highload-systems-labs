package ru.itmo.service.market.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.itmo.common.dto.deal.DealCreateRequestDto;
import ru.itmo.common.dto.deal.DealResponseDto;
import ru.itmo.common.dto.deal.DealStatusDto;
import ru.itmo.common.dto.deal.DealStatusUpdateRequestDto;
import ru.itmo.common.exception.NotFoundException;
import ru.itmo.modules.security.InternalAuthentication;
import ru.itmo.service.market.entity.Deal;
import ru.itmo.service.market.entity.DealStatus;
import ru.itmo.service.market.mapper.custom.DealCustomMapper;
import ru.itmo.service.market.service.DealService;

import java.util.List;

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
    @PreAuthorize("hasAuthority('BUYER') or hasAuthority('ADMIN')")
    public ResponseEntity<DealResponseDto> createDeal(
            @Valid @RequestBody DealCreateRequestDto dealCreateRequestDto,
            InternalAuthentication currentUser
    ) {
        Deal deal = dealCustomMapper.fromDto(dealCreateRequestDto);
        deal = dealService.create(deal, currentUser.getUserId());
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
            InternalAuthentication currentUser
    ) {
        Deal deal = dealService.findById(id, currentUser.getUserId()).orElseThrow(
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
            @Valid @RequestParam(value = "status", required = false) DealStatusDto status,
            Pageable pageable,
            InternalAuthentication currentUser
    ) {
        DealStatus dealStatus = dealCustomMapper.fromDto(status);
        Page<Deal> deals = dealService.findAllByStatus(currentUser.getUserId(), dealStatus, pageable);
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
    @PreAuthorize("hasAuthority('BUYER') or hasAuthority('SELLER')")
    public ResponseEntity<DealResponseDto> updateDealStatus(
            @PathVariable("id") Long id,
            @Valid @RequestBody DealStatusUpdateRequestDto dealStatusUpdateRequestDto,
            InternalAuthentication currentUser
    ) {
        DealStatusDto status = dealStatusUpdateRequestDto.getStatus();
        DealStatus dealStatus = dealCustomMapper.fromDto(status);
        Deal deal = dealService.update(id, currentUser.getUserId(), dealStatus).orElseThrow(
                () -> new NotFoundException("Deal with id %s not found".formatted(id))
        );
        return ResponseEntity.ok(
                dealCustomMapper.toDto(deal)
        );
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/users/{user_id}/deals",
            produces = {"application/json"}
    )
    public ResponseEntity<Page<DealResponseDto>> getUserDeals(
            @PathVariable("user_id") Long userId,
            @Valid @RequestParam(value = "status", required = false) DealStatusDto status,
            Pageable pageable
    ) {
        DealStatus dealStatus = dealCustomMapper.fromDto(status);
        Page<Deal> deals = dealService.findAllByStatus(userId, dealStatus, pageable);
        return ResponseEntity.ok(
                deals.map(dealCustomMapper::toDto)
        );
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/deals/in",
            produces = {"application/json"}
    )
    public ResponseEntity<List<DealResponseDto>> getDealByIds(
            @RequestParam("ids") List<Long> ids
    ) {
        List<Deal> listings = dealService.findByIds(ids);
        List<DealResponseDto> listingResponses = listings.stream()
                .map(dealCustomMapper::toDto)
                .toList();
        return ResponseEntity.ok(listingResponses);
    }
}
