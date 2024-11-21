package ru.itmo.modules.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReactiveSecurityModuleConfig {
    @Bean
    public ReactiveInternalAuthenticationFilter reactiveInternalAuthenticationFilter() {
        return new ReactiveInternalAuthenticationFilter();
    }

    @Bean
    public UserFeignClientInterceptor userFeignClientInterceptor() {
        return new UserFeignClientInterceptor();
    }
}
