package ru.itmo.service.user.service;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.common.service.exceptions.DuplicateException;
import ru.itmo.service.user.entity.UserEntity;
import ru.itmo.service.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Optional<UserEntity> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserEntity> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public boolean existsById(Long id) {
        return id != null && repository.existsById(id);
    }

    @Override
    @Transactional
    public UserEntity create(UserEntity user)   {
        if (repository.existsByName(user.getName())) {
            throw new DuplicateException("User with username " + user.getName() + " already exists");
        }
        return repository.save(user);
    }

    @Override
    @Transactional
    public Optional<UserEntity> update(UserEntity user) {
        if (existsById(user.getId())) {
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
