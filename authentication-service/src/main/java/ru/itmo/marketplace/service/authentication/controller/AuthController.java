package ru.itmo.marketplace.service.authentication.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import ru.itmo.common.dto.user.AuthTokenRequestDto;
import ru.itmo.common.dto.user.AuthTokenResponseDto;
import ru.itmo.common.dto.user.ValidateTokenRequestDto;
import ru.itmo.common.dto.user.ValidateTokenResponseDto;
import ru.itmo.marketplace.service.authentication.entity.User;
import ru.itmo.marketplace.service.authentication.service.AuthService;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final ReactiveAuthenticationManager authenticationManager;


    @Operation(summary = "Проверка валидности токена", description = "Позволяет проверить токен и получить ID пользователя и его роль.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Токен валиден",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidateTokenResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Токен недействителен или истек"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос")
    })
    @RequestMapping(
            method = RequestMethod.POST,
            value = "/validate",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    public Mono<ResponseEntity<ValidateTokenResponseDto>> validateToken(
            @Valid @RequestBody ValidateTokenRequestDto validateTokenRequestDto
    ) {
        return authService.extractUserAndValidate(validateTokenRequestDto.getToken())
                .map(it -> ResponseEntity.ok(
                        ValidateTokenResponseDto.builder()
                                .userId(it.getId())
                                .userRoleDto(it.getRole().name())
                                .build()
                ));
    }

    @Operation(summary = "Авторизация пользователя", description = "Аутентифицирует пользователя и выдает JWT-токен.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный вход",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthTokenResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Неверные учетные данные"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос")
    })
    @RequestMapping(
            method = RequestMethod.POST,
            value = "/login",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    public Mono<ResponseEntity<AuthTokenResponseDto>> login(
            @Valid @RequestBody AuthTokenRequestDto authTokenRequestDto
    ) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authTokenRequestDto.getUsername(),
                        authTokenRequestDto.getPassword()
                )
        ).flatMap(authentication -> {
            User user = (User) authentication.getPrincipal();
            String token = authService.generateToken(user);
            return Mono.just(ResponseEntity.ok(
                    AuthTokenResponseDto.builder()
                            .token(token)
                            .build()
            ));
        });
    }

}
