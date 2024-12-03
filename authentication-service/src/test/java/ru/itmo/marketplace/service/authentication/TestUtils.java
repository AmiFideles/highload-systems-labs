package ru.itmo.marketplace.service.authentication;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.itmo.marketplace.service.authentication.entity.User;
import ru.itmo.marketplace.service.authentication.entity.UserRole;
import ru.itmo.marketplace.service.authentication.repository.UserRepository;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class TestUtils {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User createUserWithPassword(String username, String password) {
        return userRepository.save(User.builder()
                        .email(createRandomString())
                        .password(passwordEncoder.encode(password))
                        .name(username)
                        .role(UserRole.SELLER)
                        .build())
                .block();
    }

    public User createSeller() {
        return userRepository.save(User.builder()
                .email(createRandomString())
                .password(createRandomString())
                .name(createRandomString())
                .role(UserRole.SELLER)
                .build())
                .block();
    }

    public User createBuyer() {
        return userRepository.save(User.builder()
                .email(createRandomString())
                .password(createRandomString())
                .name(createRandomString())
                .role(UserRole.BUYER)
                .build())
                .block();
    }

    public User createModerator() {
        return userRepository.save(User.builder()
                .email(createRandomString())
                .password(createRandomString())
                .name(createRandomString())
                .role(UserRole.MODERATOR)
                .build())
                .block();
    }

    public User createAdmin() {
        return userRepository.save(User.builder()
                .email(createRandomString())
                .password(createRandomString())
                .name(createRandomString())
                .role(UserRole.ADMIN)
                .build())
                .block();
    }
    
    public String createRandomString() {
        return UUID.randomUUID().toString();
    }

    public int createRandomInt() {
        return ThreadLocalRandom.current().nextInt(-100000, 100000 + 1);
    }

    public int createRandomPositiveInt() {
        return Math.abs(createRandomInt() + 1);
    }
}
