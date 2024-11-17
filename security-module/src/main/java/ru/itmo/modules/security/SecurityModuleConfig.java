package ru.itmo.modules.security;

import org.springframework.context.annotation.Bean;

public class SecurityModuleConfig {
    @Bean
    public InternalAuthenticationFilter internalAuthenticationFilter() {
        return new InternalAuthenticationFilter();
    }

    @Bean
    public UserFeignClientInterceptor userFeignClientInterceptor() {
        return new UserFeignClientInterceptor();
    }
}
