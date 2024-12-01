package ru.itmo.marketplace.service.review.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.itmo.common.dto.deal.DealResponseDto;

import java.util.List;

public interface DealService {

    Mono<DealResponseDto> getDeal(Long dealId);

    Flux<DealResponseDto> getAllDealsByIds(List<Long> dealsIds);

}
