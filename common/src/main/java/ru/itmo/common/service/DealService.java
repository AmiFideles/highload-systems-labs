package ru.itmo.common.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.itmo.common.model.Deal;
import ru.itmo.common.model.DealStatus;

public interface DealService {
    Optional<Deal> findById(Long dealId, Long userId);

    Page<Deal> findAllByStatus(Long userId, DealStatus dealStatus, Pageable pageable);

    Deal create(Deal deal, Long userId);

    Optional<Deal> update(Long dealId, Long userId, DealStatus dealStatus);

}
