package ru.itmo.marketplace.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.marketplace.api.CategoriesApi;
import ru.itmo.marketplace.entity.Category;
import ru.itmo.marketplace.mapper.mapstruct.CategoryMapper;
import ru.itmo.marketplace.model.CategoryPageableResponseDto;
import ru.itmo.marketplace.model.CategoryRequestDto;
import ru.itmo.marketplace.model.CategoryResponseDto;
import ru.itmo.marketplace.service.CategoryService;
import ru.itmo.marketplace.service.exceptions.NotFoundException;

@Slf4j
@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class CategoriesApiController implements CategoriesApi {
    private final CategoryMapper mapper;
    private final CategoryService service;

    @Override
    public ResponseEntity<CategoryPageableResponseDto> getList(Pageable pageable) {
        Page<Category> categories = service.findAll(pageable);
        return ResponseEntity.ok(
                mapper.toDto(categories)
        );
    }

    @Override
    public ResponseEntity<CategoryResponseDto> get(Long id) {
        var category = service.findById(id).orElseThrow(
                () -> new NotFoundException("Category with id %s not found".formatted(id))
        );
        return ResponseEntity.ok(
                mapper.toDto(category)
        );
    }

    @Override
    public ResponseEntity<CategoryResponseDto> create(CategoryRequestDto categoryRequestDto) {
        var category = mapper.fromDto(categoryRequestDto);
        category = service.create(category);
        return ResponseEntity.ok(
                mapper.toDto(category)
        );
    }

    @Override
    public ResponseEntity<CategoryResponseDto> update(Long id, CategoryRequestDto categoryRequestDto) {
        var category = mapper.fromDto(categoryRequestDto);
        category.setId(id);
        category = service.update(category).orElseThrow(
                () -> new NotFoundException("Category with id %s not found".formatted(id))
        );
        return ResponseEntity.ok(
                mapper.toDto(category)
        );
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        if (!service.deleteById(id)) {
            throw new NotFoundException("Category with id %s not found".formatted(id));
        }
        return ResponseEntity.ok().build();
    }
}
