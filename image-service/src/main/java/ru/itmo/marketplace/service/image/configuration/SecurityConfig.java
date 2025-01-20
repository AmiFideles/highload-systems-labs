package ru.itmo.marketplace.service.image.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.itmo.common.exception.ControllerAdvice;
import ru.itmo.modules.security.InternalAuthenticationFilter;
import ru.itmo.modules.security.SecurityModuleConfig;

@EnableWebSecurity
@EnableMethodSecurity
@Import({
        SecurityModuleConfig.class,
        ControllerAdvice.class,
})
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
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            InternalAuthenticationFilter authenticationFilter
    ) throws Exception {
        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                .authorizeHttpRequests()
                .requestMatchers("/error").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/api/v1/images/**").authenticated()
                .requestMatchers(AUTH_WHITELIST).permitAll()
                .and()

                .build();
    }
}

