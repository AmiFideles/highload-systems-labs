package ru.itmo.marketplace.service.authentication.controller;

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
