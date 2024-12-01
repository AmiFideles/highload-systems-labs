package ru.itmo.marketplace.service.review.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.itmo.common.dto.deal.DealResponseDto;
import ru.itmo.common.dto.listing.ListingResponseDto;
import ru.itmo.marketplace.service.review.service.DealService;
import ru.itmo.service.market.client.DealApiReactiveClient;
import ru.itmo.service.market.client.ListingApiReactiveClient;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DealClientService implements DealService {

    private final DealApiReactiveClient dealApiReactiveClient;

    @Override
    public Mono<DealResponseDto> getDeal(Long dealId) {
        return dealApiReactiveClient.getDealById(dealId);
    }

    @Override
    public Flux<DealResponseDto> getAllDealsByIds(List<Long> dealsIds) {
        return dealApiReactiveClient.getDealsByIds(dealsIds);
    }

}
