package ru.itmo.service.user.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;

public class ApplicationConfig {
    @Bean
    public PageableHandlerMethodArgumentResolverCustomizer pageableResolverCustomizer() {
        return pageableResolver -> pageableResolver.setMaxPageSize(50);
    }
}
