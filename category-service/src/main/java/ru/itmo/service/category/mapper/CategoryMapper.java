package ru.itmo.service.category.mapper;

import org.mapstruct.Mapper;
import ru.itmo.common.dto.category.CategoryRequestDto;
import ru.itmo.common.dto.category.CategoryResponseDto;
import ru.itmo.service.category.entity.Category;

@Mapper(
        componentModel = "spring"
)
public interface CategoryMapper {
    Category fromDto(CategoryRequestDto requestDto);
    CategoryResponseDto toDto(Category category);
}
