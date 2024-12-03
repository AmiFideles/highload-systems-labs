package ru.itmo.marketplace.service.authentication.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.itmo.common.dto.user.ValidateTokenRequestDto;
import ru.itmo.common.dto.user.ValidateTokenResponseDto;

@FeignClient(name = "authentication-service", path = "/api/v1/auth")
public interface AuthenticationServiceClient {

    @PostMapping(
            value = "/validate",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    ValidateTokenResponseDto validate(
            @Valid @RequestBody ValidateTokenRequestDto validateTokenRequestDto
    );

}
