package ru.itmo.common.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.itmo.common.model.DealReview;

public interface DealReviewService{
    Optional<DealReview> findById(Long dealId);
    Page<DealReview> findAll(Long userId, Pageable pageable);
    DealReview create(DealReview dealReview, Long userId);
    Optional<DealReview> update(DealReview dealReview, Long userId);
    boolean deleteById(Long dealId, Long userId);
}
