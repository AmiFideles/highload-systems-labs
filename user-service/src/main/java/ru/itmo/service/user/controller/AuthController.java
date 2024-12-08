package ru.itmo.service.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
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
    @Autowired
    private JwtService authService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper mapper;
    @Autowired
    private AuthenticationManager authenticationManager;

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/user/{id}",
            produces = {"application/json"}
    )
    public ResponseEntity<UserAuthDto> getAuthUserById(
            @PathVariable("id") Long id
    ) {
        User user = userService.findById(id).orElseThrow(
                () -> new NotFoundException("User with id %s not found".formatted(id))
        );
        return ResponseEntity.ok(mapper.toSecurityDto(user));
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/login",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    public ResponseEntity<AuthTokenResponseDto> login(
            @Valid @RequestBody AuthTokenRequestDto authTokenRequestDto
    ) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authTokenRequestDto.getUsername(),
                        authTokenRequestDto.getPassword()
                )
        );
        if (!authenticate.isAuthenticated()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid credentials"
            );
        }
        User user = (User) authenticate.getPrincipal();
        String token = authService.generateToken(user);
        return ResponseEntity.ok(
                AuthTokenResponseDto.builder()
                        .token(token)
                        .build()
        );
    }

}
