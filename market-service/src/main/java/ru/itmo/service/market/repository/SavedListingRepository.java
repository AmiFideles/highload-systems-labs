package ru.itmo.service.market.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.itmo.service.market.entity.SavedListing;

import java.util.Optional;

@Repository
public interface SavedListingRepository extends JpaRepository<SavedListing, Long> {
    Optional<SavedListing> findByListingIdAndUserId(Long listingId, Long userId);
    Page<SavedListing> findAllByUserId(Long userId, Pageable pageable);
    int deleteByListingIdAndUserId(Long listingId, Long userId);
    Optional<SavedListing> findByUserIdAndListingId(Long userId, Long listingId);
}
