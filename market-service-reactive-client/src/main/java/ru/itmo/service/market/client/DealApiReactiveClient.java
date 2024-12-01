package ru.itmo.service.market.client;

import org.springframework.cloud.openfeign.CollectionFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.itmo.common.dto.deal.DealResponseDto;

import java.util.List;

@ReactiveFeignClient(name = "market-service", path = "/api/v1/deals")
public interface DealApiReactiveClient {

    @GetMapping(
            value = "/{id}",
            produces = {"application/json"}
    )
    Mono<DealResponseDto> getDealById(
            @PathVariable("id") Long id
    );

    @GetMapping(
            value = "/in",
            produces = {"application/json"}
    )
    @CollectionFormat(feign.CollectionFormat.CSV)
    Flux<DealResponseDto> getDealsByIds(
            @RequestParam("ids") List<Long> ids
    );

}
