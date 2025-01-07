package ru.itmo.marketplace.service.notification.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.itmo.common.dto.listing.ListingUnavailableNotificationDto;
import ru.itmo.marketplace.service.notification.dto.NotificationDto;
import ru.itmo.marketplace.service.notification.service.UserNotificationService;

@Slf4j
@Service
@RequiredArgsConstructor
public class SavedListingConsumer {
    private final UserNotificationService userNotificationService;

    @KafkaListener(
            topics = "${app.kafka.topics.saved-listing-unavailable}",
            properties = {"spring.json.value.default.type=ru.itmo.common.dto.listing.ListingUnavailableNotificationDto"}
    )
    public void listenListingUnavailable(@Payload ListingUnavailableNotificationDto notificationDto) {
        log.info("Received ListingUnavailableNotificationDto: {}", notificationDto);

        String formattedMessage = buildNotificationMessage(notificationDto);

        notificationDto.getUserId().forEach(userId -> {
            log.info("Sending notification to user with ID: {}", userId);
            userNotificationService.sendNotification(
                    NotificationDto.builder()
                            .userId(userId)
                            .message(formattedMessage)
                            .build()
            );
        });
    }

    private String buildNotificationMessage(ListingUnavailableNotificationDto notificationDto) {
        return String.format(
                """
                        Dear buyer,
                                               \s
                        We regret to inform you that the listing "%s", which you saved, is no longer available.
                        Please try exploring similar listings on our platform.
                                               \s
                        Best regards,
                        Your Marketplace Team
                       \s""",
                notificationDto.getListingTitle()
        );
    }
}
