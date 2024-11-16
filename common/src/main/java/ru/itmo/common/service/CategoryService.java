package ru.itmo.common.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.itmo.common.model.Category;


public interface CategoryService {
    Optional<Category> findById(long id);

    Page<Category> findAll(Pageable pageable);

    Category create(Category category);

    Optional<Category> update(Category category);

    boolean deleteById(long id);
}
