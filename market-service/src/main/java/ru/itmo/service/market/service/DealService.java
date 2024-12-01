package ru.itmo.service.market.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.itmo.service.market.entity.Deal;
import ru.itmo.service.market.entity.DealStatus;

import java.util.List;
import java.util.Optional;

public interface DealService {
    Optional<Deal> findById(Long dealId, Long userId);

    Page<Deal> findAllByStatus(Long userId, DealStatus dealStatus, Pageable pageable);

    Deal create(Deal deal, Long userId);

    Optional<Deal> update(Long dealId, Long userId, DealStatus dealStatus);

    List<Deal> findByIds(List<Long> ids);
}
