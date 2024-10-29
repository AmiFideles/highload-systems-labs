package ru.itmo.marketplace.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.marketplace.api.UsersApi;
import ru.itmo.marketplace.entity.Deal;
import ru.itmo.marketplace.entity.DealStatus;
import ru.itmo.marketplace.entity.Listing;
import ru.itmo.marketplace.entity.User;
import ru.itmo.marketplace.mapper.custom.DealCustomMapper;
import ru.itmo.marketplace.mapper.custom.ListingCustomMapper;
import ru.itmo.marketplace.mapper.mapstruct.UserMapper;
import ru.itmo.marketplace.model.*;
import ru.itmo.marketplace.service.DealService;
import ru.itmo.marketplace.service.ListingService;
import ru.itmo.marketplace.service.UserService;
import ru.itmo.marketplace.service.exceptions.NotFoundException;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class UsersApiController implements UsersApi {
    private final UserMapper mapper;
    private final UserService userService;
    private final DealService dealService;
    private final ListingCustomMapper listingCustomMapper;
    private final DealCustomMapper dealCustomMapper;
    private final ListingService listingService;

    @Override
    public ResponseEntity<UserResponseDto> createUser(UserRequestDto userRequestDto) {
        User user = mapper.fromDto(userRequestDto);
        userService.create(user);
        return ResponseEntity.ok(mapper.toDto(user));
    }

    @Override
    public ResponseEntity<UserResponseDto> getUserById(Long id) {
        User user = userService.findById(id).orElseThrow(
                () -> new NotFoundException("User with id %s not found".formatted(id))
        );
        return ResponseEntity.ok(mapper.toDto(user));
    }

    @Override
    public ResponseEntity<UserPageableResponseDto> getUserList(Pageable pageable) {
        Page<User> users = userService.findAll(pageable);
        return ResponseEntity.ok(mapper.toDto(users));
    }

    @Override
    public ResponseEntity<UserResponseDto> updateUser(Long id, UserRequestDto userRequestDto) {
        User user = mapper.fromDto(userRequestDto);
        user.setId(id);

        user = userService.update(user).orElseThrow(
                () -> new NotFoundException("User with id %s not found".formatted(id))
        );

        return ResponseEntity.ok(mapper.toDto(user));
    }

    @Override
    public ResponseEntity<Void> deleteUser(Long id) {
        if (!userService.deleteById(id)) {
            throw new NotFoundException("User with id %s not found".formatted(id));
        }
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<DealPageableResponseDto> getUserDeals(Long userId, DealStatusDto status, Pageable pageable) {
        DealStatus dealStatus = dealCustomMapper.fromDto(status);
        Page<Deal> deals = dealService.findAllByStatus(userId, dealStatus, pageable);
        return ResponseEntity.ok(
                dealCustomMapper.toDto(deals)
        );
    }

    @Override
    public ResponseEntity<ListingPageableResponseDto> getUserListings(
            Long xUserId, ListingStatusDto status, Long userId, Pageable pageable
    ) {
        Long idUser = Optional.ofNullable(userId).orElse(xUserId);
        var listingStatus = listingCustomMapper.fromDto(status);
        Page<Listing> listings = listingService.findByUserIdAndStatus(idUser, listingStatus, pageable);
        return ResponseEntity.ok(
                listingCustomMapper.toDto(listings)
        );
    }
}
