package ru.itmo.marketplace.service.notification.config;

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
public class SecurityConfiguration {
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
                .requestMatchers("/ws-server/**").permitAll()
                .requestMatchers("/ws/**").authenticated()

                .requestMatchers("/api/v1/**").authenticated() // TODO: remove
                .and()

                .build();
    }
}
