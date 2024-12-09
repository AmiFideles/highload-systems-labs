package ru.itmo.marketplace.service.review.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.itmo.common.dto.user.UserResponseDto;
import ru.itmo.marketplace.service.review.service.UserService;
import ru.itmo.service.user.client.UserApiReactiveClient;

import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserClientService implements UserService {
    private final UserApiReactiveClient userServiceClient;

    @Override
    public Mono<UserResponseDto> findById(Long id) {
        return userServiceClient.getUserById(id)
                .onErrorMap(originalError -> {
                    if (originalError.getCause() instanceof IllegalArgumentException) {
                        return new ResponseStatusException(
                                HttpStatus.SERVICE_UNAVAILABLE,
                                "Unable to find user-service"
                        );
                    }
                    return originalError;
                });
    }

    @Override
    public Flux<UserResponseDto> findByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Flux.empty();
        }
        return userServiceClient.getUsersByIds(new ArrayList<>(ids))
                .onErrorMap(originalError -> {
                    if (originalError.getCause() instanceof IllegalArgumentException) {
                        return new ResponseStatusException(
                                HttpStatus.SERVICE_UNAVAILABLE,
                                "Unable to find user-service"
                        );
                    }
                    return originalError;
                });
    }
}
