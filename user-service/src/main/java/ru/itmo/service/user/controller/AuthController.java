package ru.itmo.service.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import ru.itmo.common.dto.user.AuthTokenRequestDto;
import ru.itmo.common.dto.user.AuthTokenResponseDto;
import ru.itmo.common.dto.user.UserAuthDto;
import ru.itmo.common.exception.NotFoundException;
import ru.itmo.service.user.entity.User;
import ru.itmo.service.user.mapper.UserMapper;
import ru.itmo.service.user.service.JwtService;
import ru.itmo.service.user.service.UserService;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JwtService authService;
    private final UserService userService;
    private final UserMapper mapper;
    private final ReactiveAuthenticationManager authenticationManager;

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/user/{id}",
            produces = {"application/json"}
    )
    public Mono<ResponseEntity<UserAuthDto>> getAuthUserById(
            @PathVariable("id") Long id
    ) {
        return userService.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("User not found")))
                .map(user -> ResponseEntity.ok(mapper.toSecurityDto(user)));
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
            if (!authentication.isAuthenticated()) {
                return Mono.error(new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid credentials"
                ));
            }
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
