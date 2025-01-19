package ru.itmo.service.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.itmo.common.dto.user.UserRequestDto;
import ru.itmo.common.dto.user.UserResponseDto;
import ru.itmo.common.exception.AccessDeniedException;
import ru.itmo.common.exception.NotFoundException;
import ru.itmo.modules.security.InternalAuthentication;
import ru.itmo.service.user.entity.User;
import ru.itmo.service.user.entity.UserRole;
import ru.itmo.service.user.mapper.UserMapper;
import ru.itmo.service.user.service.UserService;

import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UsersApiController {
    private final UserMapper mapper;
    private final UserService userService;

    @Operation(summary = "Создание нового пользователя", description = "Только администраторы могут создавать пользователей.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь успешно создан",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (не администратор)")
    })
    @RequestMapping(
            method = RequestMethod.POST,
            value = "",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    public Mono<ResponseEntity<UserResponseDto>> createUser(
            @AuthenticationPrincipal InternalAuthentication authentication,
            @Valid @RequestBody UserRequestDto userRequestDto
    ) {
        if (!Objects.equals(authentication.getUserRole(), UserRole.ADMIN.name())) {
            return Mono.error(new AccessDeniedException("Only admins can create users"));
        }
        return userService.create(mapper.fromDto(userRequestDto))
                .map(user -> ResponseEntity.ok(mapper.toDto(user)));
    }

    @Operation(summary = "Получение пользователя по ID", description = "Возвращает данные пользователя по его идентификатору.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/{id}",
            produces = {"application/json"}
    )
    public Mono<ResponseEntity<UserResponseDto>> getUserById(
            @PathVariable("id") Long id
    ) {
        return userService.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("User not found")))
                .map(user -> ResponseEntity.ok(mapper.toDto(user)));
    }


    @Operation(summary = "Получение списка пользователей по ID", description = "Возвращает список пользователей по указанным идентификаторам.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователи найдены",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Некоторые пользователи не найдены")
    })
    @GetMapping(
            value = "/in",
            produces = {"application/json"}
    )
    public Mono<ResponseEntity<List<UserResponseDto>>> getUserByIds(
            @RequestParam("ids") List<Long> ids
    ) {
        return userService.findByIds(ids)
                .collectList()
                .flatMap(users -> {
                    if (users.size() != ids.size()) {
                        return Mono.error(new NotFoundException("Some users were not found"));
                    }
                    return Mono.just(users.stream().map(mapper::toDto).toList());
                })
                .switchIfEmpty(Mono.error(new NotFoundException("User not found")))
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Получение списка пользователей с пагинацией", description = "Позволяет получать пользователей постранично.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Страница с пользователями",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)))
    })
    @RequestMapping(
            method = RequestMethod.GET,
            value = "",
            produces = {"application/json"}
    )
    public Mono<ResponseEntity<Page<UserResponseDto>>> getUserList(
            Pageable pageable
    ) {
        return userService.findAll(pageable)
                .map(page -> ResponseEntity.ok(page.map(mapper::toDto)));
    }

    @Operation(summary = "Обновление данных пользователя", description = "Позволяет обновить данные пользователя по его ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь обновлен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/{id}",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    public Mono<ResponseEntity<UserResponseDto>> updateUser(
            @AuthenticationPrincipal InternalAuthentication authentication,
            @PathVariable("id") Long id,
            @Valid @RequestBody UserRequestDto userRequestDto
    ) {
        User user = mapper.fromDto(userRequestDto);
        user.setId(id);

        return userService.update(authentication.getUserId(), user)
                .switchIfEmpty(Mono.error(new NotFoundException("User with id %s not found".formatted(id))))
                .map(updatedUser -> ResponseEntity.ok(mapper.toDto(updatedUser)));
    }

}
