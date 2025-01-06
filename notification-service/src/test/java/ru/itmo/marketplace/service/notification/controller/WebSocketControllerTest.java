package ru.itmo.marketplace.service.notification.controller;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.itmo.common.dto.review.deal.DealReviewCreatedNotificationDto;
import ru.itmo.common.dto.review.seller.SellerReviewCreatedNotificationDto;

@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
class WebSocketControllerTest {

    @Autowired
    WebSocketController webSocketController;

    @Test
    @SneakyThrows
    void testHandleDealReviewCreatedNotification() {
        webSocketController.handleDealReviewCreatedNotification(
                DealReviewCreatedNotificationDto.builder()
                        .buyerName("buyer")
                        .comment("test comment")
                        .listingTitle("test title")
                        .rating(1)
                        .sellerId(1L)
                        .build()
        );
    }

    @Test
    @SneakyThrows
    void testHandleSellerReviewCreatedNotification() {
        webSocketController.handleSellerReviewCreatedNotification(
                SellerReviewCreatedNotificationDto.builder()
                        .rating(1)
                        .comment("test comment")
                        .sellerId(1L)
                        .buyerName("buyer")
                        .build()
        );
    }

}
