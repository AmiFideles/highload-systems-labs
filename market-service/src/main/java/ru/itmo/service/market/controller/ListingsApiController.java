package ru.itmo.service.market.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.itmo.common.dto.ApiErrorDto;
import ru.itmo.common.dto.listing.*;
import ru.itmo.common.exception.NotFoundException;
import ru.itmo.modules.security.InternalAuthentication;
import ru.itmo.service.market.entity.Listing;
import ru.itmo.service.market.entity.ListingStatus;
import ru.itmo.service.market.mapper.custom.ListingCustomMapper;
import ru.itmo.service.market.service.ListingFilter;
import ru.itmo.service.market.service.ListingService;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class ListingsApiController {
    private final ListingService listingService;
    private final ListingCustomMapper listingMapper;

    @Operation(summary = "Создать объявление",
            description = "Создает новое объявление, доступно только продавцам")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное создание объявления",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ListingResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDto.class)))
    })
    @RequestMapping(
            method = RequestMethod.POST,
            value = "/listings",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    @PreAuthorize("hasAuthority('SELLER')")
    public ResponseEntity<ListingResponseDto> createListing(
            @Valid @RequestBody ListingRequestDto listingRequestDto,
            InternalAuthentication currentUser
    ) {
        Listing listing = listingMapper.fromDto(listingRequestDto);
        listing = listingService.create(listing, currentUser.getUserId());
        return ResponseEntity.ok(
                listingMapper.toDto(listing)
        );
    }

    @Operation(summary = "Получить объявление по ID",
            description = "Возвращает информацию об объявлении по его ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Объявление найдено",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ListingResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Объявление не найдено",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDto.class))) // Add error DTO
    })
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/listings/{id}",
            produces = {"application/json"}
    )
    public ResponseEntity<ListingResponseDto> getListingById(
            @PathVariable("id") Long id
    ) {
        Listing listing = listingService.findById(id).orElseThrow(
                () -> new NotFoundException("Listing with id %s not found".formatted(id))
        );
        return ResponseEntity.ok(
                listingMapper.toDto(listing)
        );
    }

    @Operation(summary = "Обновить объявление",
            description = "Обновляет данные объявления, доступно только продавцам")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Объявление обновлено",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ListingResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Объявление не найдено",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDto.class))), // Add error DTO
            @ApiResponse(responseCode = "403", description = "Доступ запрещен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDto.class))) // Add error DTO
    })
    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/listings/{id}",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    @PreAuthorize("hasAuthority('SELLER')")
    public ResponseEntity<ListingResponseDto> updateListing(
            @PathVariable("id") Long id,
            @Valid @RequestBody ListingRequestDto listingRequestDto,
            InternalAuthentication currentUser
    ) {
        Listing updatedListing = listingMapper.fromDto(listingRequestDto);
        updatedListing.setId(id);
        updatedListing = listingService.update(updatedListing, currentUser.getUserId()).orElseThrow(
                () -> new NotFoundException("Listing with id %s not found".formatted(id))
        );
        return ResponseEntity.ok(
                listingMapper.toDto(updatedListing)
        );
    }

    @Operation(summary = "Удалить объявление",
            description = "Удаляет объявление по ID, доступно только продавцам")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Объявление удалено"),
            @ApiResponse(responseCode = "404", description = "Объявление не найдено",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDto.class)))
    })
    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/listings/{id}",
            produces = {"application/json"}
    )
    @PreAuthorize("hasAuthority('SELLER')")
    public ResponseEntity<Void> deleteListing(
            @PathVariable("id") Long id,
            InternalAuthentication currentUser
    ) {
        if (!listingService.deleteById(id, currentUser.getUserId())) {
            throw new NotFoundException("Listing with id %s not found".formatted(id));
        }
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Поиск объявлений",
            description = "Позволяет фильтровать объявления по различным параметрам")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Результаты поиска",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ListingResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры для фильтрации",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDto.class))) // Add error DTO
    })
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/listings",
            produces = {"application/json"}
    )
    public ResponseEntity<Page<ListingResponseDto>> searchListings(
            @Valid @RequestParam(value = "max_price", required = false) Double maxPrice,
            @Valid @RequestParam(value = "min_price", required = false) Double minPrice,
            @Valid @RequestParam(value = "categories_in", required = false) List<Long> categoriesIn,
            @Valid @RequestParam(value = "is_used", required = false) Boolean isUsed,
            Pageable pageable
    ) {
        ListingFilter listingFilter = ListingFilter.builder()
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .categoriesIn(categoriesIn)
                .isUsed(isUsed).build();
        Page<Listing> listings = listingService.findAll(listingFilter, pageable);
        return ResponseEntity.ok(
                listings.map(listingMapper::toDto)
        );
    }

    @Operation(summary = "Получить открытые объявления",
            description = "Возвращает открытые объявления для модераторов и администраторов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список открытых объявлений",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ListingResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDto.class)))
    })
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/listings/open",
            produces = {"application/json"}
    )
    @PreAuthorize("hasAuthority('MODERATOR') or hasAuthority('ADMIN')")
    public ResponseEntity<Page<ListingResponseDto>> getOpenListings(
            Pageable pageable
    ) {
        Page<Listing> openListings = listingService.findOpenListings(pageable);
        return ResponseEntity.ok(
                openListings.map(listingMapper::toDto)
        );
    }

    @Operation(summary = "Обновить статус объявления",
            description = "Позволяет модератору или администратору изменить статус объявления")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Статус объявления обновлен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ModeratedListingResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Объявление не найдено",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDto.class)))
    })
    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/listings/{id}/status",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    @PreAuthorize("hasAuthority('MODERATOR') or hasAuthority('ADMIN')")
    public ResponseEntity<ModeratedListingResponseDto> updateListingStatus(
            @PathVariable("id") Long id,
            @Valid @RequestBody ModeratorListingRequestDto moderatorListingRequestDto
    ) {
        ListingStatus listingStatus = listingMapper.fromDto(moderatorListingRequestDto.getStatus());
        Optional<Listing> updatedListing = listingService.updateStatusById(listingStatus, id);
        updatedListing.orElseThrow(
                () -> new NotFoundException("Listing with id %s not found".formatted(id))
        );
        return ResponseEntity.ok(
                listingMapper.toModeratedDto(updatedListing.get())
        );
    }

    @Operation(summary = "Получить объявления пользователя",
            description = "Возвращает список объявлений пользователя по статусу")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список объявлений пользователя",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ListingResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDto.class)))
    })
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/listings/seller",
            produces = {"application/json"}
    )
    @PreAuthorize("hasAuthority('SELLER')")
    public ResponseEntity<Page<ListingResponseDto>> getUserListings(
            @Valid @RequestParam(value = "status", required = false) ListingStatusDto status,
            Pageable pageable,
            InternalAuthentication currentUser
    ) {
//        Long idUser = Optional.ofNullable(userId).orElse(xUserId);
        ListingStatus listingStatus = listingMapper.fromDto(status);
        Page<Listing> listings = listingService.findByUserIdAndStatus(currentUser.getUserId(), listingStatus, pageable);
        return ResponseEntity.ok(
                listings.map(listingMapper::toDto)
        );
    }
}
