package ru.itmo.common.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

public interface CrudService<T, ID> {
    Optional<T> findById(ID id);

    boolean existsById(@Nullable ID id);

    Page<T> findAll(Pageable pageable);

    T create(T entity);

    Optional<T> update(T entity);

    boolean deleteById(ID id);
}
