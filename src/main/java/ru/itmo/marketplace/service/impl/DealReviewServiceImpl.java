package ru.itmo.marketplace.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.marketplace.entity.Deal;
import ru.itmo.marketplace.entity.DealReview;
import ru.itmo.marketplace.entity.Role;
import ru.itmo.marketplace.entity.User;
import ru.itmo.marketplace.repository.DealReviewRepository;
import ru.itmo.marketplace.service.DealReviewService;
import ru.itmo.marketplace.service.DealService;
import ru.itmo.marketplace.service.UserService;
import ru.itmo.marketplace.service.exceptions.AccessDeniedException;
import ru.itmo.marketplace.service.exceptions.DuplicateException;
import ru.itmo.marketplace.service.exceptions.NotFoundException;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DealReviewServiceImpl implements DealReviewService {
    private final DealReviewRepository dealReviewRepository;
    private final UserService userService;
    private final DealService dealService;


    @Override
    @Transactional(readOnly = true)
    public Optional<DealReview> findById(Long dealId) {
        return dealReviewRepository.findById(dealId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DealReview> findAll(Long userId, Pageable pageable) {
        User user = userService.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id %s not found".formatted(userId))
        );
        if (user.getRole() != Role.BUYER) {
            throw new AccessDeniedException("Only buyer has deal reviews");
        }
        return dealReviewRepository.findAllByBuyerId(userId, pageable);
    }

    @Override
    @Transactional
    public DealReview create(DealReview dealReview, Long userId) {
        Deal deal = dealReview.getDeal();
        User buyer = deal.getBuyer();
        if (!buyer.getId().equals(userId)) {
            throw new AccessDeniedException("User with ID " + userId + " is not allowed to create a review for deal");
        }

        Optional<DealReview> existingReview = dealReviewRepository.findById(deal.getId());
        if (existingReview.isPresent()) {
            throw new DuplicateException("A review for this deal already exists.");
        }

        dealReview.setId(deal.getId());
        return dealReviewRepository.save(dealReview);
    }

    @Override
    @Transactional
    public Optional<DealReview> update(DealReview dealReview, Long userId) {
        Optional<DealReview> existingReviewOptional = dealReviewRepository.findById(dealReview.getId());

        if (existingReviewOptional.isPresent()) {
            DealReview existingReview = existingReviewOptional.get();

            Long buyerId = existingReview.getDeal().getBuyer().getId();
            if (buyerId.equals(userId)) {
                dealReview.setCreatedAt(existingReview.getCreatedAt());
                return Optional.of(dealReviewRepository.save(dealReview));
            } else {
                throw new AccessDeniedException("User does not have permission to update this review.");
            }
        }

        return Optional.empty();
    }

    @Override
    @Transactional
    public boolean deleteById(Long dealReviewId, Long userId) {
        Optional<DealReview> reviewOpt = dealReviewRepository.findById(dealReviewId);
        if (reviewOpt.isPresent()) {
            DealReview review = reviewOpt.get();
            Long buyerId = review.getDeal().getBuyer().getId();
            User user = userService.findById(userId).orElseThrow(
                    () -> new NotFoundException("User with id %s not found".formatted(userId)));
            if (user.getRole() == Role.MODERATOR || buyerId.equals(userId)) {
                return dealReviewRepository.removeById(dealReviewId) > 0;
            } else {
                throw new AccessDeniedException("User does not have permission to delete this review.");
            }
        }
        return false;
    }
}
