package ru.itmo.marketplace.service.notification.consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.itmo.common.dto.listing.ListingStatusChangedNotificationDto;
import ru.itmo.common.dto.listing.ListingStatusDto;
import ru.itmo.marketplace.service.notification.dto.NotificationDto;
import ru.itmo.marketplace.service.notification.service.UserNotificationService;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ListingUpdatedConsumerTest {

    @Mock
    private UserNotificationService userNotificationService;

    private ListingUpdatedConsumer listingUpdatedConsumer;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        listingUpdatedConsumer = new ListingUpdatedConsumer(userNotificationService);
        doNothing().when(userNotificationService).sendNotification(any(NotificationDto.class));
    }

    @Test
    void shouldSendNotificationOnListingStatusUpdated() {
        ListingStatusChangedNotificationDto notificationDto = new ListingStatusChangedNotificationDto();
        notificationDto.setCreatorId(123L);
        notificationDto.setTitle("ListingTitle");
        notificationDto.setListingStatusDto(ListingStatusDto.OPEN);

        listingUpdatedConsumer.listenListingUpdated(notificationDto);

        ArgumentCaptor<NotificationDto> notificationCaptor = ArgumentCaptor.forClass(NotificationDto.class);
        verify(userNotificationService, times(1)).sendNotification(notificationCaptor.capture());

        assertEquals(123L, notificationCaptor.getValue().userId());
        String message = notificationCaptor.getValue().message();
        assertTrue(message.contains("ListingTitle"));
        assertTrue(message.contains("OPEN"));
    }
}