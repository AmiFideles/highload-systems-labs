package ru.itmo.service.user.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.itmo.common.dto.user.UserRequestDto;
import ru.itmo.common.dto.user.UserResponseDto;

@FeignClient(name = "users-service", path = "/api/v1")
public interface UserServiceClient {
    @RequestMapping(
            method = RequestMethod.POST,
            value = "/users",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    UserResponseDto createUser(
            @Valid @RequestBody UserRequestDto userRequestDto
    );

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/users/{id}",
            produces = {"application/json"}
    )
    UserResponseDto getUserById(
            @PathVariable("id") Long id
    );

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/users",
            produces = {"application/json"}
    )
    Page<UserResponseDto> getUserList(
            Pageable pageable
    );

    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/users/{id}",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    UserResponseDto updateUser(
            @PathVariable("id") Long id,
            @Valid @RequestBody UserRequestDto userRequestDto
    );

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/users/{id}",
            produces = {"application/json"}
    )
    Void deleteUser(
            @PathVariable("id") Long id
    );
}

