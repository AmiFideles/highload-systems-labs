package ru.itmo.modules.security;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Getter
public class InternalAuthentication extends AbstractAuthenticationToken {

    private final Long userId;
    private final String userRole;

    public InternalAuthentication(Long userId, String role) {
        super(List.of(new SimpleGrantedAuthority(role)));
        this.userId = userId;
        this.userRole = role;
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
