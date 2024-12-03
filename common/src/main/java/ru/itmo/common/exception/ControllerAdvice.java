package ru.itmo.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.itmo.common.dto.ApiErrorDto;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorDto> handleBadCredentialsException(BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(
                        ApiErrorDto.builder()
                                .code(ApiErrorCode.UNAUTHORIZED.name())
                                .message(e.getMessage())
                                .build()
                );
    }

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

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorDto> handleApiException(ApiException e) {
        return ResponseEntity.status(HttpStatus.resolve(e.getErrorCode().toHttpStatus()))
                .body(
                        ApiErrorDto.builder()
                                .code(e.getErrorCode().name())
                                .message(e.getMessage())
                                .build()
                );
    }
}