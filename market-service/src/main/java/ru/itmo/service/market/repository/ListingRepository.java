package ru.itmo.service.market.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import ru.itmo.service.market.entity.Listing;
import ru.itmo.service.market.entity.ListingStatus;


@Repository
public interface ListingRepository extends JpaRepository<Listing, Long>, JpaSpecificationExecutor<Listing> {
    @Modifying
    Integer removeById(Long id);
    Page<Listing> findByStatus(ListingStatus status, Pageable pageable);
    Page<Listing> findByStatusAndCreatorId(ListingStatus status, Long creatorId, Pageable pageable);
    Page<Listing> findByCreatorId(Long creatorId, Pageable pageable);
}
