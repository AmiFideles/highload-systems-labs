package ru.itmo.service.market.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.common.exception.AccessDeniedException;
import ru.itmo.common.exception.DuplicateException;
import ru.itmo.service.market.entity.Deal;
import ru.itmo.service.market.entity.DealStatus;
import ru.itmo.service.market.entity.Listing;
import ru.itmo.service.market.entity.ListingStatus;
import ru.itmo.service.market.repository.DealRepository;
import ru.itmo.service.market.service.DealService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DealServiceImpl implements DealService {
    private final DealRepository dealRepository;

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
        return dealRepository.findAllByBuyerIdAndStatus(userId, dealStatus, pageable);
    }

    @Override
    @Transactional
    public Deal create(Deal deal, Long userId) {
        deal.setBuyerId(userId);
        Listing listing = deal.getListing();
        if (listing.getStatus() != ListingStatus.OPEN) {
            throw new IllegalArgumentException("Listing with id %s is in wrong status: %s".formatted(
                    listing.getId(),
                    listing.getStatus()
            ));
        }
        Optional<Deal> existingDeal = dealRepository.findByBuyerIdAndListing(userId, listing);
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

    @Override
    @Transactional(readOnly = true)
    public List<Deal> findByIds(List<Long> ids) {
        return dealRepository.findAllById(ids);
    }

    private boolean isCorrectStatusTransition(Deal deal, DealStatus dealStatus) {
        DealStatus status = deal.getStatus();
        if (status == DealStatus.PENDING) {
            return dealStatus == DealStatus.COMPLETED || dealStatus == DealStatus.CANCELED;
        }
        return false;
    }

    private boolean hasAccessRights(Deal deal, Long userId) {
        return deal.getBuyerId().equals(userId) || deal.getListing().getCreatorId().equals(userId);
    }
}
