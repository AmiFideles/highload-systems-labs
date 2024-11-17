package ru.itmo.marketplace.controller;

import java.util.Optional;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.common.dto.user.UserRequestDto;
import ru.itmo.common.dto.user.UserResponseDto;
import ru.itmo.marketplace.dto.DealResponseDto;
import ru.itmo.marketplace.dto.DealStatusDto;
import ru.itmo.marketplace.dto.ListingResponseDto;
import ru.itmo.marketplace.dto.ListingStatusDto;
import ru.itmo.marketplace.entity.Deal;
import ru.itmo.marketplace.entity.DealStatus;
import ru.itmo.marketplace.entity.Listing;
import ru.itmo.marketplace.mapper.custom.DealCustomMapper;
import ru.itmo.marketplace.mapper.custom.ListingCustomMapper;
import ru.itmo.marketplace.mapper.mapstruct.UserMapper;
import ru.itmo.marketplace.service.DealService;
import ru.itmo.marketplace.service.ListingService;
import ru.itmo.marketplace.service.UserService;
import ru.itmo.service.user.client.UserServiceClient;

@Slf4j
@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class UsersApiController {
    private final UserMapper mapper;
    private final UserService userService;
    private final DealService dealService;
    private final ListingCustomMapper listingCustomMapper;
    private final DealCustomMapper dealCustomMapper;
    private final ListingService listingService;
    private final UserServiceClient userServiceClient;

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/users",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    public ResponseEntity<UserResponseDto> createUser(
            @Valid @RequestBody UserRequestDto userRequestDto
    ) {
        return ResponseEntity.ok(
                userServiceClient.createUser(userRequestDto)
        );
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/users/{id}",
            produces = {"application/json"}
    )
    public ResponseEntity<UserResponseDto> getUserById(
            @PathVariable("id") Long id
    ) {
        return ResponseEntity.ok(userServiceClient.getUserById(id));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/users",
            produces = {"application/json"}
    )
    public ResponseEntity<Page<UserResponseDto>> getUserList(
            Pageable pageable
    ) {
        return ResponseEntity.ok(userServiceClient.getUserList(pageable));
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/users/{id}",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable("id") Long id,
            @Valid @RequestBody UserRequestDto userRequestDto
    ) {
        return ResponseEntity.ok(userServiceClient.updateUser(id, userRequestDto));
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/users/{id}",
            produces = {"application/json"}
    )
    public ResponseEntity<Void> deleteUser(
            @PathVariable("id") Long id
    ) {
        userServiceClient.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/users/{user_id}/deals",
            produces = {"application/json"}
    )
    public ResponseEntity<Page<DealResponseDto>> getUserDeals(
            @PathVariable("user_id") Long userId,
            @Valid @RequestParam(value = "status", required = false) DealStatusDto status,
            Pageable pageable
    ) {
        DealStatus dealStatus = dealCustomMapper.fromDto(status);
        Page<Deal> deals = dealService.findAllByStatus(userId, dealStatus, pageable);
        return ResponseEntity.ok(
                deals.map(dealCustomMapper::toDto)
        );
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/users/listings",
            produces = {"application/json"}
    )
    public ResponseEntity<Page<ListingResponseDto>> getUserListings(
            @NotNull @RequestHeader(value = "X-User-Id", required = true) Long xUserId,
            @Valid @RequestParam(value = "status", required = false) ListingStatusDto status,
            @Valid @RequestParam(value = "user_id", required = false) Long userId,
            Pageable pageable
    ) {
        Long idUser = Optional.ofNullable(userId).orElse(xUserId);
        var listingStatus = listingCustomMapper.fromDto(status);
        Page<Listing> listings = listingService.findByUserIdAndStatus(idUser, listingStatus, pageable);
        return ResponseEntity.ok(
                listings.map(listingCustomMapper::toDto)
        );
    }
}
