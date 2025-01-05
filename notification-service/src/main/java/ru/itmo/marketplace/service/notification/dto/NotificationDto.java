package ru.itmo.marketplace.service.notification.dto;

public record NotificationDto(
        Long userId,
        String message
) {
}
