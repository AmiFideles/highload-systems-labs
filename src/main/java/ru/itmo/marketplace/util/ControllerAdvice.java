package ru.itmo.marketplace.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.itmo.marketplace.model.ApiErrorDto;
import ru.itmo.marketplace.service.exceptions.AccessDeniedException;
import ru.itmo.marketplace.service.exceptions.DuplicateException;
import ru.itmo.marketplace.service.exceptions.NotFoundException;

@RestControllerAdvice
public class ControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ApiErrorDto> handleDuplicateException(DuplicateException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        ApiErrorDto.builder()
                                .code(ApiErrorCode.DUPLICATE.name())
                                .message(e.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorDto> handleNotFoundException(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(
                        ApiErrorDto.builder()
                                .code(ApiErrorCode.NOT_FOUND.name())
                                .message(e.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorDto> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        ApiErrorDto.builder()
                                .code(ApiErrorCode.CLIENT_ERROR.name())
                                .message(e.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorDto> handleAccessDenied(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(
                        ApiErrorDto.builder()
                                .code(ApiErrorCode.NO_ACCESS.name())
                                .message(e.getMessage())
                                .build()
                );
    }
}
