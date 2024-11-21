package ru.itmo.service.listing.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.itmo.service.listing.entity.ListingEntity;
import ru.itmo.service.listing.repository.ListingRepository;

@Slf4j
@RestController
@RequestMapping("/api/v1/listings")
@RequiredArgsConstructor
public class ListingApiController {
    private final ListingRepository listingRepository;

    @RequestMapping(
            method = RequestMethod.GET,
            value = "",
            produces = {"application/json"}
    )
    public Mono<ResponseEntity<Page<ListingEntity>>> getList(
            Pageable pageable
    ) {
        // TODO: Просто пример. Объект надо замапить.
        return listingRepository.findAllBy(pageable)
                .collectList()
                .zipWith(listingRepository.count())
                .map(p -> ResponseEntity.ok(new PageImpl<>(p.getT1(), pageable, p.getT2())));
    }
}
