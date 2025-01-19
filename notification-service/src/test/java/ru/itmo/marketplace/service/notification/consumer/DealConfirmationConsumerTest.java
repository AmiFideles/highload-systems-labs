package ru.itmo.marketplace.service.notification.consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.itmo.common.dto.deal.DealConfirmationDto;
import ru.itmo.common.dto.deal.DealStatusDto;
import ru.itmo.marketplace.service.notification.dto.NotificationDto;
import ru.itmo.marketplace.service.notification.service.UserNotificationService;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class DealConfirmationConsumerTest {

    @Mock
    private UserNotificationService userNotificationService;

    private DealConfirmationConsumer dealConfirmationConsumer;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        dealConfirmationConsumer = new DealConfirmationConsumer(userNotificationService);

        doNothing().when(userNotificationService).sendNotification(any(NotificationDto.class));
    }

    @Test
    void shouldSendNotificationOnDealCompletion() {
        DealConfirmationDto dealConfirmationDto = new DealConfirmationDto();
        dealConfirmationDto.setBuyerId(123L);
        dealConfirmationDto.setSellerName("SellerName");
        dealConfirmationDto.setListingTitle("ListingTitle");
        dealConfirmationDto.setDealStatusDto(DealStatusDto.COMPLETED);

        dealConfirmationConsumer.listenDealConfirmation(dealConfirmationDto);

        ArgumentCaptor<NotificationDto> notificationCaptor = ArgumentCaptor.forClass(NotificationDto.class);
        verify(userNotificationService, times(1)).sendNotification(notificationCaptor.capture());

        NotificationDto notification = notificationCaptor.getValue();
        assertEquals(dealConfirmationDto.getBuyerId(), notification.userId());
        assertTrue(notification.message().contains("Congratulations! The seller SellerName has agreed to complete the deal for the listing \"ListingTitle\"."));
    }

    @Test
    void shouldSendNotificationOnDealCancellation() {
        DealConfirmationDto dealConfirmationDto = new DealConfirmationDto();
        dealConfirmationDto.setBuyerId(123L);
        dealConfirmationDto.setSellerName("SellerName");
        dealConfirmationDto.setListingTitle("ListingTitle");
        dealConfirmationDto.setDealStatusDto(DealStatusDto.CANCELED);

        dealConfirmationConsumer.listenDealConfirmation(dealConfirmationDto);

        ArgumentCaptor<NotificationDto> notificationCaptor = ArgumentCaptor.forClass(NotificationDto.class);
        verify(userNotificationService, times(1)).sendNotification(notificationCaptor.capture());

        NotificationDto notification = notificationCaptor.getValue();
        assertEquals(dealConfirmationDto.getBuyerId(), notification.userId());
        assertTrue(notification.message().contains("Unfortunately, the seller SellerName has declined the deal for the listing \"ListingTitle\"."));
    }
}