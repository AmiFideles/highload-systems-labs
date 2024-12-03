package ru.itmo.common.exception;

public enum ApiErrorCode {
    SERVER_ERROR(500),
    CLIENT_ERROR(400),

    NO_ACCESS(403),
    NOT_FOUND(404),
    DUPLICATE(400),
    UNAUTHORIZED(401),

    TOKEN_EXPIRED(401),
    TOKEN_INVALID(401);

    private final int httpCode;

    ApiErrorCode(int code) {
        this.httpCode = code;;
    }

    public int toHttpStatus() {
        return httpCode;
    }
}
