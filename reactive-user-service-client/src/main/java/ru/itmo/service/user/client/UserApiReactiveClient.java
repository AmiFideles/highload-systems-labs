package ru.itmo.service.user.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.CollectionFormat;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.itmo.common.dto.user.UserRequestDto;
import ru.itmo.common.dto.user.UserResponseDto;

import java.util.List;

@Component
@ReactiveFeignClient(name = "users-service", path = "/api/v1")
public interface UserApiReactiveClient {

    @PostMapping(value = "/users", produces = "application/json", consumes = "application/json")
    Mono<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto userRequestDto);

    @GetMapping(value = "/users/{id}", produces = "application/json")
    Mono<UserResponseDto> getUserById(@PathVariable("id") Long id);

    @GetMapping(value = "/users/in", produces = "application/json")
    @CollectionFormat(feign.CollectionFormat.CSV)
    Flux<UserResponseDto> getUsersByIds(@RequestParam("ids") List<Long> ids);

    @GetMapping(value = "/users", produces = "application/json")
    Flux<UserResponseDto> getUserList();

    @PutMapping(value = "/users/{id}", produces = "application/json", consumes = "application/json")
    Mono<UserResponseDto> updateUser(
            @PathVariable("id") Long id,
            @Valid @RequestBody UserRequestDto userRequestDto
    );

    @DeleteMapping(value = "/users/{id}", produces = "application/json")
    Mono<Void> deleteUser(@PathVariable("id") Long id);

}
