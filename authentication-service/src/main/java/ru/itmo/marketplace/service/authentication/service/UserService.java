package ru.itmo.marketplace.service.authentication.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.itmo.marketplace.service.authentication.entity.User;
import ru.itmo.marketplace.service.authentication.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;

    @Transactional(readOnly = true)
    public Mono<User> findById(Long id) {
        return repository.findById(id);
    }
}
