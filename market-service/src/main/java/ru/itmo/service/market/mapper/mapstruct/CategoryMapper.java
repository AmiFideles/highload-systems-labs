package ru.itmo.service.market.mapper.mapstruct;

import org.mapstruct.Mapper;
import ru.itmo.common.dto.category.CategoryRequestDto;
import ru.itmo.common.dto.category.CategoryResponseDto;
import ru.itmo.service.market.entity.Category;

@Mapper(
        componentModel = "spring"
)
public interface CategoryMapper {
    Category fromDto(CategoryRequestDto requestDto);
    CategoryResponseDto toDto(Category category);
}
