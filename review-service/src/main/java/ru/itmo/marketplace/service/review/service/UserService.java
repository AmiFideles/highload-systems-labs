package ru.itmo.marketplace.service.review.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.itmo.common.dto.user.UserResponseDto;

import java.util.Collection;

public interface UserService {
    Mono<UserResponseDto> findById(Long id);

    Flux<UserResponseDto> findByIds(Collection<Long> ids);
}
