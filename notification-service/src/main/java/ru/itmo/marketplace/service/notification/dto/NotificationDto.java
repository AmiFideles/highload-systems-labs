package ru.itmo.marketplace.service.notification.dto;

import lombok.Builder;

@Builder
public record NotificationDto(
        Long userId,
        String message
) {
}
