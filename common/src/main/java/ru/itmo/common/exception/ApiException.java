package ru.itmo.common.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    private final ApiErrorCode errorCode;

    public ApiException(ApiErrorCode errorCode) {
        super();
        this.errorCode = errorCode;
    }

    public ApiException(ApiErrorCode errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }
}
