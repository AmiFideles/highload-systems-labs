package ru.itmo.modules.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserSecurityContextHolder {
    public String getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        InternalAuthentication internalAuthentication = (InternalAuthentication) authentication;
        return internalAuthentication.getUserId().toString();
    }

    public String getUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        InternalAuthentication internalAuthentication = (InternalAuthentication) authentication;
        return internalAuthentication.getUserRole();
    }
}
