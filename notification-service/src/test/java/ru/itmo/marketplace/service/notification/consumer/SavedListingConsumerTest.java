package ru.itmo.marketplace.service.notification.consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.itmo.common.dto.listing.ListingUnavailableNotificationDto;
import ru.itmo.marketplace.service.notification.dto.NotificationDto;
import ru.itmo.marketplace.service.notification.service.UserNotificationService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class SavedListingConsumerTest {

    @Mock
    private UserNotificationService userNotificationService;

    private SavedListingConsumer savedListingConsumer;

    @BeforeEach
    public void setup() {
        // Инициализация моков
        MockitoAnnotations.openMocks(this);
        savedListingConsumer = new SavedListingConsumer(userNotificationService);

        doNothing().when(userNotificationService).sendNotification(any(NotificationDto.class));
    }

    @Test
    void shouldSendNotificationOnListingUnavailable() {
        ListingUnavailableNotificationDto notificationDto = new ListingUnavailableNotificationDto();
        notificationDto.setListingTitle("Listing Title");
        notificationDto.setUserId(Arrays.asList(123L, 456L));

        savedListingConsumer.listenListingUnavailable(notificationDto);

        ArgumentCaptor<NotificationDto> notificationCaptor = ArgumentCaptor.forClass(NotificationDto.class);
        verify(userNotificationService, times(2)).sendNotification(notificationCaptor.capture());

        assertEquals(123L, notificationCaptor.getAllValues().get(0).userId());
        assertEquals(456L, notificationCaptor.getAllValues().get(1).userId());
    }

    @Test
    void shouldHandleEmptyUserIdList() {
        ListingUnavailableNotificationDto notificationDto = new ListingUnavailableNotificationDto();
        notificationDto.setListingTitle("Listing Title");
        notificationDto.setUserId(List.of());

        savedListingConsumer.listenListingUnavailable(notificationDto);

        verify(userNotificationService, times(0)).sendNotification(any(NotificationDto.class));
    }
}