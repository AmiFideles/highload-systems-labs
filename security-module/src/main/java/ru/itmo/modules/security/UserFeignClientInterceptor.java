package ru.itmo.modules.security;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class UserFeignClientInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Enriching request with headers: %s".formatted(authentication.getName()));
        if (authentication != null && authentication.isAuthenticated()) {
            InternalAuthentication internalAuthentication = (InternalAuthentication) authentication;
            template.header("X-User-Id", internalAuthentication.getUserId().toString());
            template.header("X-User-Role", internalAuthentication.getUserRole());
        }
    }
}
