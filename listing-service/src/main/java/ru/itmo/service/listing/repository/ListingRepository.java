package ru.itmo.service.listing.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.itmo.service.listing.entity.Listing;
import ru.itmo.service.listing.entity.ListingStatus;

@Repository
public interface ListingRepository extends ReactiveCrudRepository<Listing, Long> {
    //TODO фильтр по категориям?
    @Query("""
        SELECT * FROM listing
        WHERE (:minPrice IS NULL OR price >= :minPrice)
          AND (:maxPrice IS NULL OR price <= :maxPrice)
          AND (:isUsed IS NULL OR used = :isUsed)
    """)
    Flux<Listing> findFilteredListings(
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("isUsed") Boolean isUsed,
            Pageable pageable
    );
    @Modifying
    Mono<Integer> removeById(Long id);

    Flux<Listing> findByStatus(ListingStatus status, Pageable pageable);

    Flux<Listing> findByStatusAndCreatorId(ListingStatus status, Long creatorId, Pageable pageable);

    Flux<Listing> findByCreatorId(Long creatorId, Pageable pageable);
}
