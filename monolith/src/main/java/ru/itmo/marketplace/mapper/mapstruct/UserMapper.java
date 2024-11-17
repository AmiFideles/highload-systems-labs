package ru.itmo.marketplace.mapper.mapstruct;

import org.mapstruct.Mapper;
import ru.itmo.common.dto.user.UserRequestDto;
import ru.itmo.common.dto.user.UserResponseDto;
import ru.itmo.marketplace.entity.User;

@Mapper(
        componentModel = "spring"
)
public interface UserMapper {
    User fromDto(UserRequestDto userRequestDto);
    UserResponseDto toDto(User user);
}
