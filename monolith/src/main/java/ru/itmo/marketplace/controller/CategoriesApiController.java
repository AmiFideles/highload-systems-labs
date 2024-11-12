package ru.itmo.marketplace.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.marketplace.entity.Category;
import ru.itmo.marketplace.mapper.mapstruct.CategoryMapper;
import ru.itmo.marketplace.dto.CategoryRequestDto;
import ru.itmo.marketplace.dto.CategoryResponseDto;
import ru.itmo.marketplace.service.CategoryService;
import ru.itmo.marketplace.service.exceptions.NotFoundException;

@Slf4j
@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class CategoriesApiController {
    private final CategoryMapper mapper;
    private final CategoryService service;

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/categories",
            produces = {"application/json"}
    )
    public ResponseEntity<Page<CategoryResponseDto>> getList(
            Pageable pageable
    ) {
        Page<Category> categories = service.findAll(pageable);
        return ResponseEntity.ok(
                categories.map(mapper::toDto)
        );
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/categories/{id}",
            produces = {"application/json"}
    )
    public ResponseEntity<CategoryResponseDto> get(
            @PathVariable("id") Long id
    ) {
        var category = service.findById(id).orElseThrow(
                () -> new NotFoundException("Category with id %s not found".formatted(id))
        );
        return ResponseEntity.ok(
                mapper.toDto(category)
        );
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/categories",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    public ResponseEntity<CategoryResponseDto> create(
            @Valid @RequestBody CategoryRequestDto categoryRequestDto
    ) {
        var category = mapper.fromDto(categoryRequestDto);
        category = service.create(category);
        return ResponseEntity.ok(
                mapper.toDto(category)
        );
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/categories/{id}",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    public ResponseEntity<CategoryResponseDto> update(
            @PathVariable("id") Long id,
            @Valid @RequestBody CategoryRequestDto categoryRequestDto
    ) {
        var category = mapper.fromDto(categoryRequestDto);
        category.setId(id);
        category = service.update(category).orElseThrow(
                () -> new NotFoundException("Category with id %s not found".formatted(id))
        );
        return ResponseEntity.ok(
                mapper.toDto(category)
        );
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/categories/{id}",
            produces = {"application/json"}
    )
    public ResponseEntity<Void> delete(
            @PathVariable("id") Long id
    ) {
        if (!service.deleteById(id)) {
            throw new NotFoundException("Category with id %s not found".formatted(id));
        }
        return ResponseEntity.ok().build();
    }
}
