package ru.itmo.marketplace.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.marketplace.entity.*;
import ru.itmo.marketplace.repository.DealRepository;
import ru.itmo.marketplace.service.DealService;
import ru.itmo.marketplace.service.UserService;
import ru.itmo.marketplace.service.exceptions.AccessDeniedException;
import ru.itmo.marketplace.service.exceptions.DuplicateException;
import ru.itmo.marketplace.service.exceptions.NotFoundException;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DealServiceImpl implements DealService {
    private final DealRepository dealRepository;
    private final UserService userService;

    @Override
    @Transactional(readOnly = true)
    public Optional<Deal> findById(Long dealId, Long userId) {
        Optional<Deal> maybeDeal = dealRepository.findById(dealId);
        if (maybeDeal.isPresent()) {
            Deal deal = maybeDeal.get();
            if (!hasAccessRights(deal, userId)) {
                throw new AccessDeniedException("User does not have permission to view this deal.");
            }
            return Optional.of(deal);
        }
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Deal> findAllByStatus(Long userId, DealStatus dealStatus, Pageable pageable) {
        if (!userService.existsById(userId)) {
            throw new NotFoundException("User not found with id: " + userId);
        }
        return dealRepository.findAllByBuyerIdAndStatus(userId, dealStatus, pageable);
    }

    @Override
    @Transactional
    public Deal create(Deal deal, Long userId) {
        User user = userService.findById(userId).orElseThrow(
                () -> new NotFoundException("User not found with id: " + userId)
        );
        if (!user.getRole().equals(Role.BUYER)) {
            throw new AccessDeniedException("Only sellers can create listings.");
        }
        deal.setBuyer(user);
        Listing listing = deal.getListing();
        if (listing.getStatus() != ListingStatus.OPEN) {
            throw new IllegalArgumentException("Listing with id %s is in wrong status: %s".formatted(
                    listing.getId(),
                    listing.getStatus()
            ));
        }
        Optional<Deal> existingDeal = dealRepository.findByBuyerAndListing(user, listing);
        if (existingDeal.isPresent()) {
            throw new DuplicateException("A deal for this listing already exists for the buyer.");
        }
        return dealRepository.save(deal);
    }

    @Override
    @Transactional
    public Optional<Deal> update(Long dealId, Long userId, DealStatus dealStatus) {
        Optional<Deal> maybeDeal = dealRepository.findById(dealId);
        if (maybeDeal.isPresent()) {
            Deal deal = maybeDeal.get();
            if (!isCorrectStatusTransition(deal, dealStatus)) {
                throw new IllegalArgumentException("Deal status transition from %s to %s is impossible".formatted(
                        deal.getStatus(), dealStatus
                ));
            }

            if (!hasAccessRights(deal, userId)) {
                throw new AccessDeniedException("User does not have permission to update this deal.");
            }
            deal.setStatus(dealStatus);
            return Optional.of(dealRepository.save(deal));
        }
        return Optional.empty();
    }

    private boolean isCorrectStatusTransition(Deal deal, DealStatus dealStatus) {
        DealStatus status = deal.getStatus();
        if (status == DealStatus.PENDING) {
            return dealStatus == DealStatus.COMPLETED || dealStatus == DealStatus.CANCELED;
        }
        return false;
    }

    private boolean hasAccessRights(Deal deal, Long userId) {
        return deal.getBuyer().getId().equals(userId) || deal.getListing().getCreator().getId().equals(userId);
    }
}
