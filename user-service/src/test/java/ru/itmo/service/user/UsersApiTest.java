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
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.common.dto.user.UserRequestDto;
import ru.itmo.common.dto.user.UserResponseDto;
import ru.itmo.service.user.entity.User;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

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
    void getUsers__adminCallEmptyUsers_emptyReturned() {
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

    // Пример теста с проверкой статуса и валидности данных
    @Test
    @Rollback
    @SneakyThrows
    void getUsers__whenUserListRequested_thenUserListReturned() {
        User admin = testUtils.createAdmin();

        List<UserResponseDto> response = webTestClient.get()
                .uri("/api/v1/users")
                .header("X-User-Id", admin.getId().toString())
                .header("X-User-Role", admin.getRole().toString())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserResponseDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(response);
        assertFalse(response.isEmpty()); // Предполагаем, что пользователи должны существовать
    }

    @Test
    @Rollback
    @SneakyThrows
    void getUsersByIds__whenValidIdsProvided_thenUserListReturned() {
        User admin = testUtils.createAdmin();
        List<Long> userIds = List.of(1L, 2L); // Пример ID

        List<UserResponseDto> response = webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/users/in")
                        .queryParam("ids", String.join(",", userIds.stream().map(Object::toString).collect(Collectors.toList())))
                        .build())
                .header("X-User-Id", admin.getId().toString())
                .header("X-User-Role", admin.getRole().toString())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserResponseDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(response);
        assertEquals(userIds.size(), response.size());
    }

    @Test
    @Rollback
    @SneakyThrows
    void getUsersByIds__whenInvalidIdsProvided_thenNotFound() {
        User admin = testUtils.createAdmin();

        webTestClient.get()
                .uri("/api/v1/users/in?ids=9999")
                .header("X-User-Id", admin.getId().toString())
                .header("X-User-Role", admin.getRole().toString())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Rollback
    @SneakyThrows
    void getUserById__whenValidIdProvided_thenUserReturned() {
        User admin = testUtils.createAdmin();
        Long userId = 1L; // Пример существующего ID пользователя

        UserResponseDto response = webTestClient.get()
                .uri("/api/v1/users/{id}", userId)
                .header("X-User-Id", admin.getId().toString())
                .header("X-User-Role", admin.getRole().toString())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(response);
        assertEquals(userId, response.getId());
    }

    @Test
    @Rollback
    @SneakyThrows
    void getUserById__whenInvalidIdProvided_thenNotFound() {
        User admin = testUtils.createAdmin();

        webTestClient.get()
                .uri("/api/v1/users/{id}", 9999)
                .header("X-User-Id", admin.getId().toString())
                .header("X-User-Role", admin.getRole().toString())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Rollback
    @SneakyThrows
    void createUser__validInput_userCreated() {
        User admin = testUtils.createAdmin();
        UserRequestDto validUserRequest = new UserRequestDto(
                "newuser@example.com",
                "securePassword",
                "New User",
                "BUYER"
        );

        UserResponseDto response = webTestClient.post()
                .uri("/api/v1/users")
                .header("X-User-Id", admin.getId().toString())
                .header("X-User-Role", admin.getRole().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(validUserRequest))
                .exchange()
                .expectStatus().isOk() // Ожидаем успешное выполнение
                .expectBody(UserResponseDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(response);
        assertEquals(validUserRequest.getEmail(), response.getEmail());
        assertEquals(validUserRequest.getName(), response.getName());
        assertEquals(validUserRequest.getRole(), response.getRole());
    }

    @Test
    @Rollback
    @SneakyThrows
    void createUser__whenInvalidInput_thenBadRequest() {
        User admin = testUtils.createAdmin();
        UserRequestDto invalidUser = new UserRequestDto(null, "password", "Name", "USER");

        webTestClient.post()
                .uri("/api/v1/users")
                .header("X-User-Id", admin.getId().toString())
                .header("X-User-Role", admin.getRole().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(invalidUser))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Rollback
    @SneakyThrows
    void createUser__whenUserNameAlreadyExists_thenBadRequest() {
        User admin = testUtils.createAdmin();
        UserRequestDto invalidUser = new UserRequestDto(null, "password", admin.getName(), "BUYER");

        webTestClient.post()
                .uri("/api/v1/users")
                .header("X-User-Id", admin.getId().toString())
                .header("X-User-Role", admin.getRole().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(invalidUser))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Rollback
    @SneakyThrows
    void createUser__whenNotModeratorOrAdmin_thenForbidden() {
        User buyer = testUtils.createBuyer();
        UserRequestDto userRequest = new UserRequestDto(
                "test@example.com",
                "password",
                "Name",
                "BUYER"
        );

        webTestClient.post()
                .uri("/api/v1/users")
                .header("X-User-Id", buyer.getId().toString())
                .header("X-User-Role", buyer.getRole().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(userRequest))
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    @Rollback
    @SneakyThrows
    void updateUser__validInput_userUpdated() {
        User admin = testUtils.createAdmin();
        UserRequestDto validUpdateRequest = new UserRequestDto(
                "updateduser@example.com",
                "newSecurePassword",
                "Updated User",
                "BUYER"
        );

        UserResponseDto response = webTestClient.put()
                .uri("/api/v1/users/3")
                .header("X-User-Id", admin.getId().toString())
                .header("X-User-Role", admin.getRole().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(validUpdateRequest))
                .exchange()
                .expectStatus().isOk() // Ожидаем успешное выполнение
                .expectBody(UserResponseDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(response);
        assertEquals(validUpdateRequest.getEmail(), response.getEmail());
        assertEquals(validUpdateRequest.getName(), response.getName());
        assertEquals(validUpdateRequest.getRole(), response.getRole());
    }

    @Test
    @Rollback
    @SneakyThrows
    void updateUser__whenInvalidInput_thenBadRequest() {
        User admin = testUtils.createAdmin();
        UserRequestDto invalidUpdate = new UserRequestDto(
                "",
                "newPassword",
                "New Name",
                "USER"
        );

        webTestClient.put()
                .uri("/api/v1/users/1")
                .header("X-User-Id", admin.getId().toString())
                .header("X-User-Role", admin.getRole().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(invalidUpdate))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Rollback
    @SneakyThrows
    void updateUser__whenUnauthorizedAccess_thenForbidden() {
        User anotherUser = testUtils.createBuyer();
        UserRequestDto updateRequest = new UserRequestDto(
                "updated@example.com",
                "newPassword",
                "Updated Name",
                "BUYER"
        );

        webTestClient.put()
                .uri("/api/v1/users/1")
                .header("X-User-Id", anotherUser.getId().toString())
                .header("X-User-Role", anotherUser.getRole().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(updateRequest))
                .exchange()
                .expectStatus().isForbidden();
    }

}
