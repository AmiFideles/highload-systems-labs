package ru.itmo.marketplace.service.review.service;

import ru.itmo.common.dto.review.deal.DealReviewCreatedNotificationDto;
import ru.itmo.common.dto.review.seller.SellerReviewCreatedNotificationDto;

public interface NotificationSenderService {

    void sendDealReviewCreatedNotification(DealReviewCreatedNotificationDto notification);

    void sendSellerReviewCreatedNotification(SellerReviewCreatedNotificationDto notification);

}
