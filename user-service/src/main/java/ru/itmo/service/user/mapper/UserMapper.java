package ru.itmo.service.user.mapper;

import org.mapstruct.Mapper;
import ru.itmo.common.dto.user.UserRequestDto;
import ru.itmo.common.dto.user.UserResponseDto;
import ru.itmo.service.user.entity.UserEntity;

@Mapper(
        componentModel = "spring"
)
public interface UserMapper {
    UserEntity fromDto(UserRequestDto userRequestDto);
    UserResponseDto toDto(UserEntity user);
}
