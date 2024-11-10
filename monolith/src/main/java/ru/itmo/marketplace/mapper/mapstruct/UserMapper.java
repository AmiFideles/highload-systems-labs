package ru.itmo.marketplace.mapper.mapstruct;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import ru.itmo.marketplace.entity.User;
import ru.itmo.marketplace.model.UserPageableResponseDto;
import ru.itmo.marketplace.model.UserRequestDto;
import ru.itmo.marketplace.model.UserResponseDto;

@Mapper(
        componentModel = "spring"
)
public interface UserMapper {
    User fromDto(UserRequestDto userRequestDto);
    UserResponseDto toDto(User user);
    UserPageableResponseDto toDto (Page<User> user);
}
