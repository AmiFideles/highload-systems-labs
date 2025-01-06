package ru.itmo.marketplace.service.notification.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.itmo.marketplace.service.notification.dto.NotificationDto;

@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
class UserNotificationServiceTest {

    @Autowired
    UserNotificationService userNotificationService;

    @Test
    @SneakyThrows
    void testUserNotificationService() {
        userNotificationService.sendNotification(new NotificationDto(1L, "test message"));
    }

}
