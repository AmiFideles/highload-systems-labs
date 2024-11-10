package ru.itmo.marketplace.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import ru.itmo.marketplace.entity.Deal;
import ru.itmo.marketplace.entity.DealStatus;
import ru.itmo.marketplace.entity.Listing;
import ru.itmo.marketplace.entity.User;

import java.util.Optional;


@Repository
public interface DealRepository extends JpaRepository<Deal, Long> {
    @Modifying
    Integer removeById(Long id);

    Page<Deal> findAllByBuyerIdAndStatus(Long buyerId, DealStatus status, Pageable pageable);

    Optional<Deal> findByBuyerAndListing(User buyer, Listing listing);
}
