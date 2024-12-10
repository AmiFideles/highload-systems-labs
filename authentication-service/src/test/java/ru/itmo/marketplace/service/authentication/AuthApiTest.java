package ru.itmo.marketplace.service.authentication;

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
import ru.itmo.common.dto.user.AuthTokenRequestDto;
import ru.itmo.common.dto.user.AuthTokenResponseDto;
import ru.itmo.common.dto.user.ValidateTokenRequestDto;
import ru.itmo.marketplace.service.authentication.entity.User;
import ru.itmo.marketplace.service.authentication.service.AuthService;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.RequestEntity.post;

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
    private AuthService authService;

    @Autowired
    private TestUtils testUtils;

    @Test
    public void whenValidToken_thenReturns200() {
        // Настройка корректного токена
        User admin = testUtils.createAdmin();
        String token = authService.generateToken(admin);
        ValidateTokenRequestDto requestDto = new ValidateTokenRequestDto(token);

        webTestClient.post().uri("/api/v1/auth/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    public void whenInvalidToken_thenReturns401() {
        // Настройка некорректного токена
        String token = "invalidToken";
        ValidateTokenRequestDto requestDto = new ValidateTokenRequestDto(token);

        webTestClient.post().uri("/api/v1/auth/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    public void whenNoToken_thenReturns400() {
        // Настройка для случая отсутствия токена
        ValidateTokenRequestDto requestDto = new ValidateTokenRequestDto(null);

        webTestClient.post().uri("/api/v1/auth/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus()
                .isBadRequest();
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
