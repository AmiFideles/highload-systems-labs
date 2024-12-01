package ru.itmo.service.market.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.itmo.service.market.entity.Category;

import java.util.Optional;

public interface CategoryService {
    Optional<Category> findById(long id);

    Page<Category> findAll(Pageable pageable);

    Category create(Category category);

    Optional<Category> update(Category category);

    boolean deleteById(long id);
}
