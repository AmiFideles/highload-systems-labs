package ru.itmo.marketplace.service.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.itmo.marketplace.service.review.entity.SellerReviewEntity;

import java.util.List;

@Repository
public interface SellerReviewRepository extends JpaRepository<SellerReviewEntity, SellerReviewEntity.SellerReviewId> {
    boolean existsByAuthorIdAndSellerId(Long authorId, Long sellerId);

    Page<SellerReviewEntity> findByAuthorId(Long authorId, Pageable pageable);

    Page<SellerReviewEntity> findBySellerId(Long sellerId, Pageable pageable);

    int deleteByAuthorIdAndSellerId(Long authorId, Long sellerId);

    SellerReviewEntity findByAuthorIdAndSellerId(Long authorId, Long sellerId);
}
