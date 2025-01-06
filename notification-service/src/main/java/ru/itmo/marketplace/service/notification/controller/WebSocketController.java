package ru.itmo.marketplace.service.notification.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import ru.itmo.common.dto.review.deal.DealReviewCreatedNotificationDto;
import ru.itmo.common.dto.review.seller.SellerReviewCreatedNotificationDto;
import ru.itmo.marketplace.service.notification.dto.NotificationDto;
import ru.itmo.marketplace.service.notification.service.UserNotificationService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketController {
    private final UserNotificationService userNotificationService;

    @MessageMapping("/reviews/deals")
    public void handleDealReviewCreatedNotification(
            DealReviewCreatedNotificationDto notificationDto
    ) {
        log.info("DealReviewCreatedNotificationDto: {}", notificationDto);

        String text = String.format(
                """
                        Dear Seller,
                        
                        You've received a new review on your listing '%s'
                        Rating: %s out of 5
                        Comment: %s
                        
                        By user: %s
                        Best regards,
                        Your Marketplace Team
                        """,
                notificationDto.getListingTitle(),
                notificationDto.getRating(),
                notificationDto.getComment(),
                notificationDto.getBuyerName()
        );

        userNotificationService.sendNotification(
                NotificationDto.builder()
                        .userId(notificationDto.getSellerId())
                        .message(text)
                        .build()
        );
    }

    @MessageMapping("/reviews/sellers")
    public void handleSellerReviewCreatedNotification(
            SellerReviewCreatedNotificationDto notificationDto
    ) {
        log.info("SellerReviewCreatedNotificationDto: {}", notificationDto);

        String text = String.format(
                """
                        Dear Seller,
                        
                        You have received a new review on you
                        Rating: %s out of 5
                        Comment: %s
                        
                        By user: %s
                        Best regards,
                        Your Marketplace Team
                        """,
                notificationDto.getRating(),
                notificationDto.getComment(),
                notificationDto.getBuyerName()
        );

        userNotificationService.sendNotification(
                NotificationDto.builder()
                        .userId(notificationDto.getSellerId())
                        .message(text)
                        .build()
        );
    }

}
