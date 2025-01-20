package ru.itmo.marketplace.service.review.controller;

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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.itmo.common.dto.ApiErrorDto;
import ru.itmo.common.dto.review.deal.DealReviewRequestDto;
import ru.itmo.common.dto.review.deal.DealReviewResponseDto;
import ru.itmo.marketplace.service.review.service.DealReviewService;
import ru.itmo.modules.security.InternalAuthentication;

@Slf4j
@RestController
@RequestMapping("/api/v1/reviews/deals")
@RequiredArgsConstructor
public class DealReviewsApiController {
    private final DealReviewService dealReviewService;

    @Operation(summary = "Создание отзыва о сделке", description = "Создает новый отзыв о сделке от текущего пользователя (покупателя).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Отзыв успешно создан"),
            @ApiResponse(responseCode = "400", description = "Неверные данные для создания отзыва",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "403", description = "Запрещено: доступ только для покупателя")
    })
    @RequestMapping(
            method = RequestMethod.POST,
            value = "",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    @PreAuthorize("hasAuthority('BUYER')")
    public Mono<ResponseEntity<DealReviewResponseDto>> createDealReview(
            @AuthenticationPrincipal InternalAuthentication authentication,
            @Valid @RequestBody DealReviewRequestDto dealReviewRequestDto
    ) {
        return dealReviewService.createDealReview(authentication.getUserId(), dealReviewRequestDto)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Удаление отзыва о сделке", description = "Удаляет отзыв о сделке по указанному ID. Доступно для покупателя, оставившего отзыв, или администратора.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Отзыв успешно удален"),
            @ApiResponse(responseCode = "404", description = "Отзыв не найден",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "403", description = "Запрещено: доступ только для покупателя или администратора")
    })
    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/{id}",
            produces = {"application/json"}
    )
    @PreAuthorize("hasAuthority('BUYER') or hasAuthority('ADMIN')")
    public Mono<ResponseEntity<Void>> deleteDealReview(
            @AuthenticationPrincipal InternalAuthentication authentication,
            @PathVariable("id") Long id
    ) {
        return dealReviewService.deleteDealReview(authentication.getUserId(), id)
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    @Operation(summary = "Получение отзыва о сделке по ID", description = "Возвращает информацию о отзыве по его ID. Доступно для покупателя, оставившего отзыв, или администратора.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Отзыв успешно получен"),
            @ApiResponse(responseCode = "404", description = "Отзыв не найден",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "403", description = "Запрещено: доступ только для покупателя или администратора")
    })
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/{id}",
            produces = {"application/json"}
    )
    public Mono<ResponseEntity<DealReviewResponseDto>> getDealReviewById(
            @AuthenticationPrincipal InternalAuthentication authentication,
            @PathVariable("id") Long id
    ) {
        return dealReviewService.getDealReview(authentication.getUserId(), id)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Получение списка отзывов о сделках", description = "Возвращает пагинированный список отзывов о сделках текущего пользователя. Доступно для покупателя.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список отзывов успешно получен"),
            @ApiResponse(responseCode = "400", description = "Неверные параметры для фильтрации отзывов",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "403", description = "Запрещено: доступ только для покупателя")
    })
    @RequestMapping(
            method = RequestMethod.GET,
            value = "",
            produces = {"application/json"}
    )
    @PreAuthorize("hasAuthority('BUYER')")
    public Mono<ResponseEntity<Page<DealReviewResponseDto>>> getDealReviewList(
            @AuthenticationPrincipal InternalAuthentication authentication,
            Pageable pageable
    ) {
        return dealReviewService.getDealReviews(authentication.getUserId(), pageable)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Обновление отзыва о сделке", description = "Обновляет существующий отзыв о сделке по указанному ID. Доступно для покупателя, оставившего отзыв.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Отзыв успешно обновлен"),
            @ApiResponse(responseCode = "404", description = "Отзыв не найден",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "400", description = "Неверные данные для обновления отзыва",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "403", description = "Запрещено: доступ только для покупателя, оставившего отзыв")
    })
    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/{id}",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    @PreAuthorize("hasAuthority('BUYER')")
    public Mono<ResponseEntity<DealReviewResponseDto>> updateDealReview(
            @AuthenticationPrincipal InternalAuthentication authentication,
            @PathVariable("id") Long id,
            @Valid @RequestBody DealReviewRequestDto dealReviewRequestDto
    ) {
        return dealReviewService.updateDealReview(authentication.getUserId(), id, dealReviewRequestDto)
                .map(ResponseEntity::ok);
    }

}
