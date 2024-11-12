package ru.itmo.marketplace.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class DealPageableResponseDto {

    @Valid
    private List<@Valid DealResponseDto> content = new ArrayList<>();

    private Integer totalElements;

    private Integer totalPages;

    private Integer size;

    private Integer number;

    private Integer numberOfElements;

    private Boolean first;

    private Boolean last;

    private Boolean empty;

    public DealPageableResponseDto content(List<@Valid DealResponseDto> content) {
        this.content = content;
        return this;
    }

    @Valid
    @JsonProperty("content")
    public List<@Valid DealResponseDto> getContent() {
        return content;
    }

    public void setContent(List<@Valid DealResponseDto> content) {
        this.content = content;
    }

    public DealPageableResponseDto totalElements(Integer totalElements) {
        this.totalElements = totalElements;
        return this;
    }

    @JsonProperty("total_elements")
    public Integer getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Integer totalElements) {
        this.totalElements = totalElements;
    }

    public DealPageableResponseDto totalPages(Integer totalPages) {
        this.totalPages = totalPages;
        return this;
    }

    @JsonProperty("total_pages")
    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public DealPageableResponseDto size(Integer size) {
        this.size = size;
        return this;
    }

    @JsonProperty("size")
    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public DealPageableResponseDto number(Integer number) {
        this.number = number;
        return this;
    }

    @JsonProperty("number")
    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public DealPageableResponseDto numberOfElements(Integer numberOfElements) {
        this.numberOfElements = numberOfElements;
        return this;
    }

    @JsonProperty("number_of_elements")
    public Integer getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(Integer numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    public DealPageableResponseDto first(Boolean first) {
        this.first = first;
        return this;
    }

    @JsonProperty("first")
    public Boolean getFirst() {
        return first;
    }

    public void setFirst(Boolean first) {
        this.first = first;
    }

    public DealPageableResponseDto last(Boolean last) {
        this.last = last;
        return this;
    }

    @JsonProperty("last")
    public Boolean getLast() {
        return last;
    }

    public void setLast(Boolean last) {
        this.last = last;
    }

    public DealPageableResponseDto empty(Boolean empty) {
        this.empty = empty;
        return this;
    }

    @JsonProperty("empty")
    public Boolean getEmpty() {
        return empty;
    }

    public void setEmpty(Boolean empty) {
        this.empty = empty;
    }

}

