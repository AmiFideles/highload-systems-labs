package ru.itmo.marketplace.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
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
public class UserResponseDto {

    private Long id;

    private String email;

    private String name;

    private String role;

    public UserResponseDto id(Long id) {
        this.id = id;
        return this;
    }

    @NotNull
    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserResponseDto email(String email) {
        this.email = email;
        return this;
    }

    @NotNull
    @Email
    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserResponseDto name(String name) {
        this.name = name;
        return this;
    }

    @NotNull
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserResponseDto role(String role) {
        this.role = role;
        return this;
    }

    @NotNull
    @JsonProperty("role")
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}

