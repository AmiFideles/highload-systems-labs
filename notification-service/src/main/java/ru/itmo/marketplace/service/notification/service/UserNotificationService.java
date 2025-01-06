package ru.itmo.marketplace.service.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import ru.itmo.marketplace.service.notification.dto.NotificationDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotification(NotificationDto notification) {
        messagingTemplate.convertAndSendToUser(
                notification.userId().toString(),
                "/queue/notifications",
                notification.message()
        );
    }

}
