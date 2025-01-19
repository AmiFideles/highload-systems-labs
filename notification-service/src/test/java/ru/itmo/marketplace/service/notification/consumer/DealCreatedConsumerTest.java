package ru.itmo.marketplace.service.notification.consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.itmo.common.dto.deal.DealCreatedDto;
import ru.itmo.marketplace.service.notification.dto.NotificationDto;
import ru.itmo.marketplace.service.notification.service.UserNotificationService;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class DealCreatedConsumerTest {

    @Mock
    private UserNotificationService userNotificationService;

    private DealCreatedConsumer dealCreatedConsumer;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        dealCreatedConsumer = new DealCreatedConsumer(userNotificationService);
        doNothing().when(userNotificationService).sendNotification(any(NotificationDto.class));
    }

    @Test
    void shouldSendNotificationOnDealCreated() {
        DealCreatedDto dealCreatedDto = new DealCreatedDto();
        dealCreatedDto.setSellerId(123L);
        dealCreatedDto.setBuyerName("BuyerName");
        dealCreatedDto.setListingTitle("ListingTitle");

        dealCreatedConsumer.listenDealCreated(dealCreatedDto);

        ArgumentCaptor<NotificationDto> notificationCaptor = ArgumentCaptor.forClass(NotificationDto.class);
        verify(userNotificationService, times(1)).sendNotification(notificationCaptor.capture());

        assertEquals(123L, notificationCaptor.getValue().userId());
        String message = notificationCaptor.getValue().message();
        assertTrue(message.contains("Dear Seller"));
        assertTrue(message.contains("BuyerName"));
        assertTrue(message.contains("ListingTitle"));
    }
}