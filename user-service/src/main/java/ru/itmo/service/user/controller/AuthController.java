package ru.itmo.service.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.common.dto.user.AuthTokenRequestDto;
import ru.itmo.common.dto.user.AuthTokenResponseDto;
import ru.itmo.common.dto.user.UserAuthDto;
import ru.itmo.common.exception.NotFoundException;
import ru.itmo.service.user.entity.User;
import ru.itmo.service.user.mapper.UserMapper;
import ru.itmo.service.user.service.UserService;

@Slf4j
@RestController("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService service;
    private final UserMapper mapper;

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/user/{id}",
            produces = {"application/json"}
    )
    public ResponseEntity<UserAuthDto> getAuthUserById(
            @PathVariable("id") Long id
    ) {
        User user = service.findById(id).orElseThrow(
                () -> new NotFoundException("User with id %s not found".formatted(id))
        );
        return ResponseEntity.ok(mapper.toSecurityDto(user));
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/login",
            produces = {"application/json"}
    )
    public ResponseEntity<AuthTokenResponseDto> login(
            @Valid @RequestBody AuthTokenRequestDto authTokenRequestDto
    ) {
        // TODO: Implement
        return ResponseEntity.ok().build();
    }

}
