package ru.itmo.marketplace.service.review.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import ru.itmo.common.exception.ControllerAdvice;
import ru.itmo.modules.security.ReactiveInternalAuthenticationFilter;
import ru.itmo.modules.security.ReactiveSecurityModuleConfig;

@Import({
        ReactiveSecurityModuleConfig.class,
        ControllerAdvice.class,
})
@EnableReactiveMethodSecurity
@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {
    private static final String[] AUTH_WHITELIST = {
            "/swagger-ui/**",
            "/api/v1/user-management/auth-info",
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            "/v3/api-docs/**",
            "/error/**",
            "/favicon.ico",
            "/error",
            "/api/auth/**"
    };

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            ReactiveInternalAuthenticationFilter reactiveInternalAuthenticationFilter
    ) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())

                .addFilterAt(reactiveInternalAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)

                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec
                        .pathMatchers("/error").permitAll()
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers("/api/v1/auth/**").permitAll()
                        .pathMatchers("/api/v1/reviews/**").authenticated()
                        .pathMatchers(AUTH_WHITELIST).permitAll()
                )
                .build();
    }
}
