package ru.itmo.service.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.itmo.common.dto.user.UserAuthDto;

@FeignClient(name = "users-service", path = "/api/v1")
public interface AuthServiceClient {
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/auth/user/{id}",
            produces = {"application/json"}
    )
    UserAuthDto getAuthUserById(
            @PathVariable("id") Long id
    );
}
