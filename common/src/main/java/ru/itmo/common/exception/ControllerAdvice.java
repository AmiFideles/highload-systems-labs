package ru.itmo.common.exception;

import feign.FeignException;
import lombok.SneakyThrows;
import org.springframework.cloud.client.circuitbreaker.NoFallbackAvailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import ru.itmo.common.dto.ApiErrorDto;

import java.util.concurrent.TimeoutException;

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


    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiErrorDto> handleInvalidTokenException(InvalidTokenException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(
                        ApiErrorDto.builder()
                                .code(ApiErrorCode.TOKEN_INVALID.name())
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

    @ExceptionHandler({TimeoutException.class})
    public ResponseEntity<ApiErrorDto> handleGlobalException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        ApiErrorDto.builder()
                                .code(ApiErrorCode.SERVER_ERROR.toString())
                                .message(ex.getMessage())
                                .build()
                );
    }

    @SneakyThrows
    @ExceptionHandler({NoFallbackAvailableException.class})
    public ResponseEntity<ApiErrorDto> handleNoFallbackAvailableException(NoFallbackAvailableException e) {
        Throwable cause = e.getCause();
        if (cause instanceof FeignException fe) {
            return handleFeignException(fe);
        } else if (cause instanceof Exception ex) {
            return handleGlobalException(ex);
        }
        throw cause;
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ApiErrorDto> handleFeignException(FeignException ex) {
        return ResponseEntity.status(ex.status())
                .body(
                        ApiErrorDto.builder()
                                .code(ApiErrorCode.of(ex.status()).toString())
                                .message(ex.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorDto> handleResponseStatusException(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode())
                .body(
                        ApiErrorDto.builder()
                                .code(ApiErrorCode.of(ex.getStatusCode().value()).toString())
                                .message(ex.getMessage())
                                .build()
                );
    }

}
