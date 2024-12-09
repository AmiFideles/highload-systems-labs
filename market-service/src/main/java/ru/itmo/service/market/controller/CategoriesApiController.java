package ru.itmo.service.market.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.itmo.common.dto.category.CategoryRequestDto;
import ru.itmo.common.dto.category.CategoryResponseDto;
import ru.itmo.common.exception.NotFoundException;
import ru.itmo.service.market.entity.Category;
import ru.itmo.service.market.mapper.mapstruct.CategoryMapper;
import ru.itmo.service.market.service.CategoryService;

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
    @PreAuthorize("hasAuthority('ADMIN')")
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
    @PreAuthorize("hasAuthority('ADMIN')")
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

    @PreAuthorize("hasAuthority('ADMIN')")
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
