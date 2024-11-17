package ru.itmo.service.user.mapper;

import org.mapstruct.Mapper;
import ru.itmo.common.dto.user.UserAuthDto;
import ru.itmo.common.dto.user.UserRequestDto;
import ru.itmo.common.dto.user.UserResponseDto;
import ru.itmo.service.user.entity.User;

@Mapper(
        componentModel = "spring"
)
public interface UserMapper {
    User fromDto(UserRequestDto userRequestDto);
    UserResponseDto toDto(User user);
    default UserAuthDto toSecurityDto(User user) {
        return UserAuthDto.builder()
                .id(user.getId())
                .username(user.getName())
                .role(user.getRole().toString())
                .build();
    }
}
