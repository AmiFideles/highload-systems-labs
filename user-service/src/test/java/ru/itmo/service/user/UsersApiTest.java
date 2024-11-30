package ru.itmo.service.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import ru.itmo.service.user.entity.User;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class UsersApiTest extends IntegrationEnvironment {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TestUtils testUtils;

    @Test
    @SneakyThrows
    public void getUsers__adminCallEmptyUsers_emptyReturned() {
        User admin = testUtils.createAdmin();

        String response = webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/users/1")
                        .build())
                .header("X-User-Id", admin.getId().toString())
                .header("X-User-Role", admin.getRole().toString())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(String.class)
                .getResponseBody()
                .blockFirst();

        log.info("Response: {}", response);
    }
}
