package ru.itmo.service.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import ru.itmo.service.category.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Modifying
    Integer removeById(Long id);

    boolean existsByName(String name);
}