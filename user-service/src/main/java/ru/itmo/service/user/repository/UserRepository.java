package ru.itmo.service.user.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.itmo.service.user.entity.User;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {
    @Modifying
    Mono<Integer> removeById(Long id);
    Mono<Boolean> existsByName(String username);
    Mono<User> findByName(String name);
    Flux<User> findAllBy(Pageable pageable);
}
