package ru.itmo.service.listing.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.itmo.service.listing.entity.Listing;
import ru.itmo.service.listing.entity.ListingStatus;

@Repository
public interface ListingRepository extends ReactiveCrudRepository<Listing, Long> {
    Flux<Listing> findAllBy(Pageable pageable);

    Mono<Integer> removeById(Long id);

    Flux<Listing> findByStatus(ListingStatus status);

    Flux<Listing> findByStatusAndCreatorId(ListingStatus status, Long creatorId);

    Flux<Listing> findByCreatorId(Long creatorId);
}
