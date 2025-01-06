package ru.itmo.marketplace.service.review.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Service;
import ru.itmo.common.dto.review.deal.DealReviewCreatedNotificationDto;
import ru.itmo.common.dto.review.seller.SellerReviewCreatedNotificationDto;
import ru.itmo.marketplace.service.review.service.NotificationSenderService;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationSenderServiceImpl implements NotificationSenderService {
    private final StompSession wsStompSession;

    @Override
    public void sendDealReviewCreatedNotification(DealReviewCreatedNotificationDto notification) {
        wsStompSession.send("/app/reviews/deals", notification);
    }

    @Override
    public void sendSellerReviewCreatedNotification(SellerReviewCreatedNotificationDto notification) {
        wsStompSession.send("/app/reviews/sellers", notification);
    }

}
