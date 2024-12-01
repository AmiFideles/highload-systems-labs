package ru.itmo.marketplace.service.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.itmo.marketplace.service.review.entity.DealReviewEntity;

@Repository
public interface DealReviewRepository extends JpaRepository<DealReviewEntity, Long> {

    @Modifying
    Integer removeById(Long id);

    @Query(value = """
                SELECT dr FROM deal_review dr
                    JOIN deal ON dr.deal_id = deal.id
                    JOIN users ON users.id = deal.buyer_id
                    WHERE deal.buyer_id = :buyerId
            """, nativeQuery = true)
    Page<DealReviewEntity> findAllByBuyerId(@Param("buyerId") Long buyerId, Pageable pageable);

}
