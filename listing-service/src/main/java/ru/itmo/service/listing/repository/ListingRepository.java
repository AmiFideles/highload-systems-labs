package ru.itmo.service.listing.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.itmo.service.listing.entity.ListingEntity;

@Repository
public interface ListingRepository extends ReactiveSortingRepository<ListingEntity, Long> {
    Flux<ListingEntity> findAllBy(Pageable pageable);

    Mono<Long> count();
}
