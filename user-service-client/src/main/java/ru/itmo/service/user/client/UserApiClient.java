package ru.itmo.service.user.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.itmo.common.dto.user.UserRequestDto;
import ru.itmo.common.dto.user.UserResponseDto;

import java.util.List;

@FeignClient(name = "users-service", path = "/api/v1/users")
public interface UserApiClient {

    @PostMapping(
            value = "",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    UserResponseDto createUser(
            @Valid @RequestBody UserRequestDto userRequestDto
    );

    @GetMapping(
            value = "/{id}",
            produces = {"application/json"}
    )
    UserResponseDto getUserById(
            @PathVariable("id") Long id
    );

    @GetMapping(
            value = "/in",
            produces = "application/json"
    )
    List<UserResponseDto> getUsersByIds(
            @RequestParam("ids") List<Long> ids
    );

    @GetMapping(
            value = "",
            produces = {"application/json"}
    )
    Page<UserResponseDto> getUserList(
            Pageable pageable
    );

    @PutMapping(
            value = "/{id}",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    UserResponseDto updateUser(
            @PathVariable("id") Long id,
            @Valid @RequestBody UserRequestDto userRequestDto
    );

    @DeleteMapping(
            value = "/{id}",
            produces = {"application/json"}
    )
    Void deleteUser(
            @PathVariable("id") Long id
    );

}

