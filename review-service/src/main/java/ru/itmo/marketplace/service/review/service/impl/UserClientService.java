package ru.itmo.marketplace.service.review.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
        return userServiceClient.getUserById(id);
    }

    @Override
    public Flux<UserResponseDto> findByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Flux.empty();
        }
        return userServiceClient.getUsersByIds(new ArrayList<>(ids));
    }
}
