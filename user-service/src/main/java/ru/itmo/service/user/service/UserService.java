package ru.itmo.service.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.itmo.common.exception.DuplicateException;
import ru.itmo.service.user.entity.User;
import ru.itmo.service.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Mono<User> findById(Long id) {
        return repository.findById(id);
    }

    @Transactional(readOnly = true)
    public Mono<Page<User>> findAll(Pageable pageable) {
        return repository.findAllBy(pageable)
                .collectList()
                .zipWith(repository.count())
                .map(t -> new PageImpl<>(t.getT1(), pageable, t.getT2()));
    }

    public Mono<Boolean> existsById(Long id) {
        if (id == null) {
            return Mono.just(false);
        }
        return repository.existsById(id);
    }

    public Mono<Boolean> existsByName(String name) {
        if (name == null) {
            return Mono.just(false);
        }
        return repository.existsByName(name);
    }

    @Transactional
    public Mono<User> create(User user) {
        return existsByName(user.getName()).flatMap(exists -> {
            if (exists) {
                return Mono.error(new DuplicateException("User with username " + user.getName() + " already exists"));
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return repository.save(user);
        });
    }

    @Transactional
    public Mono<User> update(User user) {
        return existsById(user.getId()).flatMap(exists -> {
                    if (exists) {
                        user.setPassword(passwordEncoder.encode(user.getPassword()));
                        return repository.save(user);
                    }
                    return Mono.empty();
                }
        );
    }
}
