package ru.itmo.modules.security;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserFeignClientInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            InternalAuthentication internalAuthentication = (InternalAuthentication) authentication;
            template.header("X-User-Id", internalAuthentication.getUserId().toString());
            template.header("X-User-Role", internalAuthentication.getUserRole());
        }
    }
}
