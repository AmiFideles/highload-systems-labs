package ru.itmo.marketplace.mapper.mapstruct;

import org.mapstruct.Mapper;
import ru.itmo.marketplace.entity.Category;
import ru.itmo.marketplace.dto.CategoryRequestDto;
import ru.itmo.marketplace.dto.CategoryResponseDto;

@Mapper(
        componentModel = "spring"
)
public interface CategoryMapper {
    Category fromDto(CategoryRequestDto requestDto);
    CategoryResponseDto toDto(Category category);
}
