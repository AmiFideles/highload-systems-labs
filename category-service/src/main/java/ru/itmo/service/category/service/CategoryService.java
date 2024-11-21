package ru.itmo.service.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.common.exception.DuplicateException;
import ru.itmo.common.service.CrudService;
import ru.itmo.service.category.entity.Category;
import ru.itmo.service.category.repository.CategoryRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService implements CrudService<Category, Long> {
    private final CategoryRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Optional<Category> findById(Long id) {
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
    public boolean deleteById(Long id) {
        return repository.removeById(id) > 0;
    }

    @Override
    public boolean existsById(@Nullable Long id) {
        if (id == null) {
            return false;
        }
        return repository.existsById(id);
    }

    private boolean existsByName(String name) {
        return repository.existsByName(name);
    }
}
