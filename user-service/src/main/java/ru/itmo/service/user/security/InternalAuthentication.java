package ru.itmo.service.user.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public class InternalAuthentication extends AbstractAuthenticationToken {

    private final Long userId;

    public InternalAuthentication(Long userId, String role) {
        super(List.of(new SimpleGrantedAuthority(role)));
        this.userId = userId;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public Long getPrincipal() {
        return userId;
    }
}
