package ru.itmo.marketplace.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.itmo.marketplace.entity.SellerReview;

import java.util.Optional;

public interface SellerReviewService {
    Page<SellerReview> findByAuthorId(Long authorId, Pageable pageable);

    Page<SellerReview> findAll(Long sellerId, Pageable pageable);

    SellerReview create(SellerReview sellerReview);

    Optional<SellerReview> update(SellerReview sellerReview);

    boolean deleteById(Long authorId, Long sellerId);
}
