package ru.itmo.service.category;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.itmo.service.user.client.UserServiceClient;

@EnableFeignClients(clients = {
        UserServiceClient.class,
})
@SpringBootApplication
public class CategoryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CategoryServiceApplication.class, args);
    }
}
