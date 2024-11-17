package ru.itmo.service.user.service;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.common.exception.DuplicateException;
import ru.itmo.service.user.entity.User;
import ru.itmo.service.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public boolean existsById(Long id) {
        return id != null && repository.existsById(id);
    }

    @Override
    @Transactional
    public User create(User user)   {
        if (repository.existsByName(user.getName())) {
            throw new DuplicateException("User with username " + user.getName() + " already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return repository.save(user);
    }

    @Override
    @Transactional
    public Optional<User> update(User user) {
        if (existsById(user.getId())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return Optional.of(repository.save(user));
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        return repository.removeById(id) > 0;
    }
}
