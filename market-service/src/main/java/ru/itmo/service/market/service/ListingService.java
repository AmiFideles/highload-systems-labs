package ru.itmo.service.market.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.itmo.service.market.entity.Listing;
import ru.itmo.service.market.entity.ListingStatus;

import java.util.List;
import java.util.Optional;

public interface ListingService {

    Optional<Listing> findById(Long id);

    Page<Listing> findAll(ListingFilter listingFilter, Pageable pageable);

    Listing create(Listing listing, Long userId);

    Optional<Listing> update(Listing listing, Long userId);

    Optional<Listing> updateStatusById(ListingStatus listingStatus, Long id);

    boolean deleteById(Long listingId, Long userId);

    Page<Listing> findOpenListings(Pageable pageable);

    List<Listing> findByIds(List<Long> ids);

    Page<Listing> findByUserIdAndStatus(Long userId, ListingStatus status, Pageable pageable);
}
