package ru.itmo.service.market.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.itmo.service.market.entity.Deal;
import ru.itmo.service.market.entity.DealStatus;
import ru.itmo.service.market.entity.Listing;

import java.util.Optional;


@Repository
public interface DealRepository extends JpaRepository<Deal, Long> {
    Page<Deal> findAllByBuyerIdAndStatus(Long buyerId, DealStatus status, Pageable pageable);

    Optional<Deal> findByBuyerIdAndListing(Long buyerId, Listing listing);
}
