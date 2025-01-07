package ru.itmo.marketplace.service.notification.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.itmo.common.dto.deal.DealConfirmationDto;
import ru.itmo.marketplace.service.notification.dto.NotificationDto;
import ru.itmo.marketplace.service.notification.service.UserNotificationService;

@Slf4j
@Service
@RequiredArgsConstructor
public class DealConfirmationConsumer {

    private final UserNotificationService userNotificationService;

    @KafkaListener(
            topics = "${app.kafka.topics.deal-confirmation}",
            properties = {"spring.json.value.default.type=ru.itmo.common.dto.deal.DealConfirmationDto"}
    )
    public void listenDealConfirmation(@Payload DealConfirmationDto confirmationDto) {
        log.info("Received DealConfirmationDto: {}", confirmationDto);

        String notificationMessage = buildNotificationMessage(confirmationDto);

        log.info("Sending notification to buyer with ID: {}", confirmationDto.getBuyerId());
        userNotificationService.sendNotification(
                NotificationDto.builder()
                        .userId(confirmationDto.getBuyerId())
                        .message(notificationMessage)
                        .build()
        );
    }

    private String buildNotificationMessage(DealConfirmationDto confirmationDto) {
        return switch (confirmationDto.getDealStatusDto()) {
            case COMPLETED -> String.format(
                    """
                            Dear Buyer,
                                                           \s
                            Congratulations! The seller %s has agreed to complete the deal for the listing "%s".
                            The transaction will be completed.
                                                           \s
                            Best regards,
                            Your Marketplace Team
                           \s""",
                    confirmationDto.getSellerName(),
                    confirmationDto.getListingTitle()
            );
            case CANCELED -> String.format(
                    """
                            Dear Buyer,
                                                           \s
                            Unfortunately, the seller %s has declined the deal for the listing "%s".
                            Please explore other listings.
                                                           \s
                            Best regards,
                            Your Marketplace Team
                           \s""",
                    confirmationDto.getSellerName(),
                    confirmationDto.getListingTitle()
            );
            default -> {
                log.warn("Unsupported deal status: {}", confirmationDto.getDealStatusDto());
                yield "An unexpected status was received for your deal. Please contact support.";
            }
        };
    }
}
