package ru.itmo.marketplace.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.itmo.marketplace.entity.Category;

import java.util.Optional;

public interface CategoryService {
    Optional<Category> findById(long id);

    Page<Category> findAll(Pageable pageable);

    Category create(Category category);

    Optional<Category> update(Category category);

    boolean deleteById(long id);
}
