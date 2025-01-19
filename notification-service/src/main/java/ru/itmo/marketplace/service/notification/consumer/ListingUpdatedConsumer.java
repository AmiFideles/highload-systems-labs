package ru.itmo.marketplace.service.notification.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.itmo.common.dto.listing.ListingStatusChangedNotificationDto;
import ru.itmo.marketplace.service.notification.dto.NotificationDto;
import ru.itmo.marketplace.service.notification.service.UserNotificationService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ListingUpdatedConsumer {
    private final UserNotificationService userNotificationService;

    @KafkaListener(
            topics = "${app.kafka.topics.listing-status-updated}",
            properties = {"spring.json.value.default.type=ru.itmo.common.dto.listing.ListingStatusChangedNotificationDto"}
    )
    public void listenListingUpdated(@Payload ListingStatusChangedNotificationDto notificationDto) {
        log.info("Received ListingStatusChangedNotificationDto: {}", notificationDto);

        String message = buildNotificationMessage(notificationDto);

        userNotificationService.sendNotification(
                NotificationDto.builder()
                        .userId(notificationDto.getCreatorId())
                        .message(message)
                        .build()
        );
    }

    private String buildNotificationMessage(ListingStatusChangedNotificationDto notificationDto) {
        return String.format(
                """
                        Dear Seller,
                                               \s
                        The status of your listing %s has been updated to: %s.
                        Best regards,
                        Your Marketplace Team
                       \s""",
                notificationDto.getTitle(),
                notificationDto.getListingStatusDto()
        );
    }
}
