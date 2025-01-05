package ru.itmo.marketplace.service.notification.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.marketplace.service.notification.dto.NotificationDto;
import ru.itmo.marketplace.service.notification.service.UserNotificationService;

@Slf4j
@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class HelloController {

    private final UserNotificationService userNotificationService;

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/hello",
            produces = {"application/json"}
    )
    public ResponseEntity<?> hello() {
        userNotificationService.sendNotification(new NotificationDto(1L, "Hello"));
        return ResponseEntity.ok().build();
    }

}
