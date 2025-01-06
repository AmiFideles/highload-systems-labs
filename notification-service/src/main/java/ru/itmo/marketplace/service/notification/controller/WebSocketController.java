package ru.itmo.marketplace.service.notification.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class WebSocketController {

    @MessageMapping("/greeting")
    public void handle(String greeting) {
        log.info("Greeting: {}", greeting);
    }

}
