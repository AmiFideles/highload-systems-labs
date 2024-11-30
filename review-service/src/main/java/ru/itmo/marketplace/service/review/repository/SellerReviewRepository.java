package ru.itmo.marketplace.service.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.itmo.marketplace.service.review.entity.SellerReview;

@Repository
public interface SellerReviewRepository extends JpaRepository<SellerReview, SellerReview.SellerReviewId> {
    boolean existsByAuthorIdAndSellerId(Long authorId, Long sellerId);

    Page<SellerReview> findByAuthorId(Long authorId, Pageable pageable);

    Page<SellerReview> findBySellerId(Long sellerId, Pageable pageable);

    int deleteByAuthorIdAndSellerId(Long authorId, Long sellerId);
}
