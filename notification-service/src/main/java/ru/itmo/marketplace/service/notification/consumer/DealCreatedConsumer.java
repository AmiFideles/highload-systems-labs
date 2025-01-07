package ru.itmo.marketplace.service.notification.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.itmo.common.dto.deal.DealCreatedDto;
import ru.itmo.marketplace.service.notification.dto.NotificationDto;
import ru.itmo.marketplace.service.notification.service.UserNotificationService;

@Slf4j
@Service
@RequiredArgsConstructor
public class DealCreatedConsumer {
    private final UserNotificationService userNotificationService;

    @KafkaListener(
            topics = "${app.kafka.topics.deal-created}",
            properties = {"spring.json.value.default.type=ru.itmo.common.dto.deal.DealCreatedDto"}
    )
    public void listenDealCreated(@Payload DealCreatedDto dealCreatedDto) {
        log.info("Received DealCreatedDto: {}", dealCreatedDto);

        String formattedMessage = buildCreatedMessage(dealCreatedDto);

        log.info("Sending notification to seller with ID: {}", dealCreatedDto.getSellerId());
        userNotificationService.sendNotification(
                NotificationDto.builder()
                        .userId(dealCreatedDto.getSellerId())
                        .message(formattedMessage)
                        .build()
        );
    }

    private @NotNull String buildCreatedMessage(DealCreatedDto dealCreatedDto) {
        String messageTemplate = """
                Dear Seller,
                               \s
                A potential buyer, %s, is interested in purchasing your listing: "%s".
                Please confirm or decline the deal in your personal account.
                               \s
                Best regards,
                Your Marketplace Team
               \s""";

        return String.format(
                messageTemplate,
                dealCreatedDto.getBuyerName(),
                dealCreatedDto.getListingTitle()
        );
    }
}
