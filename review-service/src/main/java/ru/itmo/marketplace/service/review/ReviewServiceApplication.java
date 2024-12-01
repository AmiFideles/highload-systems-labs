package ru.itmo.marketplace.service.review;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactivefeign.spring.config.EnableReactiveFeignClients;
import ru.itmo.service.market.client.DealApiReactiveClient;
import ru.itmo.service.user.client.UserApiReactiveClient;

@EnableReactiveFeignClients(clients = {
        UserApiReactiveClient.class,
        DealApiReactiveClient.class,
})
@SpringBootApplication
public class ReviewServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReviewServiceApplication.class, args);
    }
}
