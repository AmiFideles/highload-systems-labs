package ru.itmo.service.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.itmo.common.dto.user.UserResponseDto;

@FeignClient(name = "users-service", path = "/api/v1/users")
public interface UserApiClient {

    @GetMapping(
            value = "/{id}",
            produces = {"application/json"}
    )
    UserResponseDto getUserById(
            @PathVariable("id") Long id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role
    );

}
