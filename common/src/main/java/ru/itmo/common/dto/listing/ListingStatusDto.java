package ru.itmo.common.dto.listing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ListingStatusDto {

    REVIEW("REVIEW"),

    OPEN("OPEN"),

    CLOSED("CLOSED"),

    DENIED("DENIED");

    private String value;

    ListingStatusDto(String value) {
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
    public static ListingStatusDto fromValue(String value) {
        for (ListingStatusDto b : ListingStatusDto.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

}

