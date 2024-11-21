package ru.itmo.modules.security;

import java.util.List;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

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
