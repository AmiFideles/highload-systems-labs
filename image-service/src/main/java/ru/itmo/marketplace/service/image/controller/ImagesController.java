package ru.itmo.marketplace.service.image.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import ru.itmo.common.dto.ApiErrorDto;
import ru.itmo.marketplace.service.image.service.MinioService;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class ImagesController {
    private final MinioService minioService;

    @Operation(summary = "Загрузка изображения", description = "Позволяет скачать изображение по имени файла.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Изображение успешно загружено",
                    content = @Content(mediaType = "application/octet-stream")),
            @ApiResponse(responseCode = "404", description = "Изображение не найдено",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "401", description = "Не авторизован",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDto.class)))
    })
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/{filename}",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE  // указываем тип файла
    )
    public ResponseEntity<InputStreamResource> downloadImage(
            @PathVariable("filename") String filename
    ) {
        InputStream inputStream = minioService.download(filename);
        InputStreamResource resource = new InputStreamResource(inputStream);
        String contentType = minioService.getFileContentType(filename);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }

    @Operation(summary = "Загрузка нового изображения", description = "Позволяет загрузить новое изображение на сервер.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Изображение успешно загружено",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Не авторизован",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDto.class)))
    })
    @RequestMapping(
            method = RequestMethod.POST,
            value = "",
            produces = MediaType.TEXT_PLAIN_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<String> uploadImage(
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        return minioService.upload(
                        file.getOriginalFilename(),
                        file.getInputStream(),
                        file.getContentType()
                )
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
    }

    @Operation(summary = "Удаление изображения", description = "Удаляет изображение с сервера по имени файла.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Изображение успешно удалено",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Изображение не найдено",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "401", description = "Не авторизован",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDto.class)))
    })
    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/{filename}",
            produces = {"application/json"}
    )
    public ResponseEntity<?> delete(
            @PathVariable("filename") String filename
    ) {
        minioService.delete(filename);
        return ResponseEntity.ok().build();
    }

}
