package ru.itmo.marketplace.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.marketplace.entity.Category;
import ru.itmo.marketplace.repository.CategoryRepository;
import ru.itmo.marketplace.service.CategoryService;
import ru.itmo.marketplace.service.exceptions.DuplicateException;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Optional<Category> findById(long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Category> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    @Transactional
    public Category create(Category category) {
        if (existsByName(category.getName())) {
            throw new DuplicateException("Category with name " + category.getName() + " already exists");
        }
        return repository.save(category);
    }

    @Override
    @Transactional
    public Optional<Category> update(Category category) {
        if (existsById(category.getId())) {
            return Optional.of(repository.save(category));
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public boolean deleteById(long id) {
        return repository.removeById(id) > 0;
    }

    private boolean existsById(@Nullable Long id) {
        if (id == null) {
            return false;
        }
        return repository.existsById(id);
    }

    private boolean existsByName(String name) {
        return repository.existsByName(name);
    }
}
