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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.itmo.common.dto.user.AuthTokenRequestDto;
import ru.itmo.common.dto.user.AuthTokenResponseDto;
import ru.itmo.common.dto.user.UserAuthDto;
import ru.itmo.service.user.entity.User;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class AuthApiTest extends IntegrationEnvironment {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TestUtils testUtils;

    @Test
    @Rollback
    @SneakyThrows
    void getAuthUserById__validId_userFound() {
        // Подготовка: создаем тестового пользователя
        Long validUserId = testUtils.createAdmin().getId();

        UserAuthDto response = webTestClient.get()
                .uri("/api/v1/auth/user/{id}", validUserId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk() // Ожидаем успешный ответ
                .expectBody(UserAuthDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(response);
        assertEquals(validUserId, response.getId());
    }

    @Test
    @Rollback
    @SneakyThrows
    void getAuthUserById__invalidId_userNotFound() {
        // Подготовка: используем несуществующий ID
        Long invalidUserId = 9999L;

        webTestClient.get()
                .uri("/api/v1/auth/user/{id}", invalidUserId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound(); // Ожидаем, что пользователь не найден
    }

    @Test
    @SneakyThrows
    void login__validCredentials_tokenReceived() {
        // Подготовка: создаем тестового пользователя
        AuthTokenRequestDto validLogin = new AuthTokenRequestDto("testUser", "testPassword");
        testUtils.createUserWithPassword(validLogin.getUsername(), validLogin.getPassword());

        AuthTokenResponseDto response = webTestClient.post()
                .uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(validLogin))
                .exchange()
                .expectStatus().isOk() // Ожидаем успешный ответ
                .expectBody(AuthTokenResponseDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(response);
        assertFalse(response.getToken().isEmpty()); // Убедимся, что токен не пустой
    }

    @Test
    @SneakyThrows
    void login__invalidCredentials_unauthorized() {
        // Подготовка: создаем тестового пользователя
        AuthTokenRequestDto invalidLogin = new AuthTokenRequestDto("invalidUser", "wrongPassword");

        webTestClient.post()
                .uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(invalidLogin))
                .exchange()
                .expectStatus().isUnauthorized(); // Ожидаем ответ об ошибке авторизации
    }

    @Test
    @SneakyThrows
    void login__invalidPassword_unauthorized() {
        // Подготовка: создаем тестового пользователя
        AuthTokenRequestDto invalidLogin = new AuthTokenRequestDto("testUser2", "testPassword");
        testUtils.createUserWithPassword(invalidLogin.getUsername(), "anotherPassword");

        webTestClient.post()
                .uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(invalidLogin))
                .exchange()
                .expectStatus().isUnauthorized(); // Ожидаем ответ об ошибке авторизации
    }

    @Test
    @SneakyThrows
    void login__invalidArguments_badRequest() {
        // Подготавливаем случай без введенных данных
        webTestClient.post()
                .uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(""))
                .exchange()
                .expectStatus().isBadRequest(); // Ожидаем некорректный запрос из-за валидации
    }

}
