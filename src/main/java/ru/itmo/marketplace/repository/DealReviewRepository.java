package ru.itmo.marketplace.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.itmo.marketplace.entity.DealReview;

@Repository
public interface DealReviewRepository extends JpaRepository<DealReview, Long> {
    @Modifying
    Integer removeById(Long id);
    @Query("SELECT dr FROM DealReview dr WHERE dr.deal.buyer.id = :buyerId")
    Page<DealReview> findAllByBuyerId(@Param("buyerId") Long buyerId, Pageable pageable);
}
