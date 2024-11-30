package ru.itmo.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.itmo.service.user.entity.User;
import ru.itmo.service.user.entity.UserRole;
import ru.itmo.service.user.repository.UserRepository;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class TestUtils {
    private final UserRepository userRepository;

    public User createSeller() {
        return userRepository.save(User.builder()
                .email(createRandomString())
                .password(createRandomString())
                .name(createRandomString())
                .role(UserRole.SELLER)
                .build());
    }

    public User createBuyer() {
        return userRepository.save(User.builder()
                .email(createRandomString())
                .password(createRandomString())
                .name(createRandomString())
                .role(UserRole.BUYER)
                .build());
    }

    public User createModerator() {
        return userRepository.save(User.builder()
                .email(createRandomString())
                .password(createRandomString())
                .name(createRandomString())
                .role(UserRole.MODERATOR)
                .build());
    }

    public User createAdmin() {
        return userRepository.save(User.builder()
                .email(createRandomString())
                .password(createRandomString())
                .name(createRandomString())
                .role(UserRole.ADMIN)
                .build());
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
