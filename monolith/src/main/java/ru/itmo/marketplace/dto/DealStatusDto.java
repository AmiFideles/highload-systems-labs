package ru.itmo.marketplace.dto;

import com.fasterxml.jackson.annotation.JsonValue;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum DealStatusDto {

    PENDING("PENDING"),

    COMPLETED("COMPLETED"),

    CANCELED("CANCELED");

    private String value;

    DealStatusDto(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static DealStatusDto fromValue(String value) {
        for (DealStatusDto b : values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}

