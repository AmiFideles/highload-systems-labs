package ru.itmo.service.user.controller;

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
import ru.itmo.common.dto.user.UserRequestDto;
import ru.itmo.common.dto.user.UserResponseDto;
import ru.itmo.common.exception.NotFoundException;
import ru.itmo.service.user.entity.User;
import ru.itmo.service.user.mapper.UserMapper;
import ru.itmo.service.user.service.UserService;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UsersApiController {
    private final UserMapper mapper;
    private final UserService userService;

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    public ResponseEntity<UserResponseDto> createUser(
            @Valid @RequestBody UserRequestDto userRequestDto
    ) {
        User user = mapper.fromDto(userRequestDto);
        userService.create(user);
        return ResponseEntity.ok(mapper.toDto(user));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/{id}",
            produces = {"application/json"}
    )
    public ResponseEntity<UserResponseDto> getUserById(
            @PathVariable("id") Long id
    ) {
        User user = userService.findById(id).orElseThrow(
                () -> new NotFoundException("User with id %s not found".formatted(id))
        );
        return ResponseEntity.ok(mapper.toDto(user));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/",
            produces = {"application/json"}
    )
    public ResponseEntity<Page<UserResponseDto>> getUserList(
            Pageable pageable
    ) {
        Page<User> users = userService.findAll(pageable);
        return ResponseEntity.ok(
                users.map(mapper::toDto)
        );
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/{id}",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable("id") Long id,
            @Valid @RequestBody UserRequestDto userRequestDto
    ) {
        User user = mapper.fromDto(userRequestDto);
        user.setId(id);

        user = userService.update(user).orElseThrow(
                () -> new NotFoundException("User with id %s not found".formatted(id))
        );

        return ResponseEntity.ok(mapper.toDto(user));
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/{id}",
            produces = {"application/json"}
    )
    public ResponseEntity<Void> deleteUser(
            @PathVariable("id") Long id
    ) {
        if (!userService.deleteById(id)) {
            throw new NotFoundException("User with id %s not found".formatted(id));
        }
        return ResponseEntity.ok().build();
    }
}
