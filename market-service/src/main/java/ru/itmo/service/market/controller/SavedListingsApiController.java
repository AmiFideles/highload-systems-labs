package ru.itmo.service.market.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.common.dto.saved.SavedListingRequestDto;
import ru.itmo.common.dto.saved.SavedListingResponseDto;
import ru.itmo.common.exception.NotFoundException;
import ru.itmo.modules.security.InternalAuthentication;
import ru.itmo.service.market.entity.SavedListing;
import ru.itmo.service.market.mapper.custom.SavedListingCustomMapper;
import ru.itmo.service.market.service.SavedListingService;

@Slf4j
@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class SavedListingsApiController {
    private final SavedListingCustomMapper savedListingMapper;
    private final SavedListingService savedListingService;

    @Operation(summary = "Добавление сохраненного объявления", description = "Создает новое сохраненное объявление для текущего пользователя.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Сохраненное объявление успешно добавлено"),
            @ApiResponse(responseCode = "400", description = "Неверные данные для добавления сохраненного объявления")
    })
    @RequestMapping(
            method = RequestMethod.POST,
            value = "/saved-listings",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    public ResponseEntity<SavedListingResponseDto> addSavedListing(
            @Valid @RequestBody SavedListingRequestDto savedListingRequestDto,
            InternalAuthentication currentUser
    ) {
        SavedListing savedListing = savedListingMapper.fromDto(savedListingRequestDto);
        savedListing.setUserId(currentUser.getUserId());

        savedListing = savedListingService.create(savedListing);

        return ResponseEntity.ok(
                savedListingMapper.toDto(savedListing)
        );
    }

    @Operation(summary = "Получение сохраненного объявления по ID", description = "Возвращает информацию о сохраненном объявлении по указанному ID. Доступно только для владельца объявления.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Сохраненное объявление успешно получено"),
            @ApiResponse(responseCode = "404", description = "Сохраненное объявление с указанным ID не найдено")
    })
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/saved-listings/{listing_id}",
            produces = {"application/json"}
    )
    public ResponseEntity<SavedListingResponseDto> getSavedListingById(
            @PathVariable("listing_id") Long listingId,
            InternalAuthentication currentUser
    ) {
        SavedListing savedListing = savedListingService.findById(listingId, currentUser.getUserId()).orElseThrow(
                () -> new NotFoundException("Saved listing with id %s not found".formatted(listingId))
        );
        return ResponseEntity.ok(
                savedListingMapper.toDto(savedListing)
        );
    }

    @Operation(summary = "Получение списка сохраненных объявлений", description = "Возвращает пагинированный список сохраненных объявлений текущего пользователя.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список сохраненных объявлений успешно получен"),
            @ApiResponse(responseCode = "400", description = "Неверные параметры для фильтрации сохраненных объявлений")
    })
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/saved-listings",
            produces = {"application/json"}
    )
    public ResponseEntity<Page<SavedListingResponseDto>> getSavedListings(
            Pageable pageable,
            InternalAuthentication currentUser
    ) {
        Page<SavedListing> savedListings = savedListingService.findAll(currentUser.getUserId(), pageable);
        return ResponseEntity.ok(
                savedListings.map(savedListingMapper::toDto)
        );
    }

    @Operation(summary = "Удаление сохраненного объявления", description = "Удаляет сохраненное объявление по указанному ID. Доступно только для владельца объявления.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Сохраненное объявление успешно удалено"),
            @ApiResponse(responseCode = "404", description = "Сохраненное объявление с указанным ID не найдено")
    })
    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/saved-listings/{listing_id}",
            produces = {"application/json"}
    )
    public ResponseEntity<Void> deleteSavedListing(
            @PathVariable("listing_id") Long listingId,
            InternalAuthentication currentUser
    ) {
        boolean deleted = savedListingService.deleteById(listingId, currentUser.getUserId());
        if (!deleted) {
            throw new NotFoundException("Saved listing with id %s not found".formatted(listingId));
        }
        return ResponseEntity.ok().build();
    }
}