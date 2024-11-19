//package ru.itmo.service.category.configuration;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Import;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import ru.itmo.modules.security.InternalAuthenticationFilter;
//import ru.itmo.modules.security.SecurityModuleConfig;
//
//@EnableWebSecurity
//@Import(SecurityModuleConfig.class)
//@Configuration
//public class SecurityConfig {
//    @Bean
//    public SecurityFilterChain securityFilterChain(
//            HttpSecurity http,
//            InternalAuthenticationFilter authenticationFilter
//    ) throws Exception {
//        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
//        return http
//                .csrf(AbstractHttpConfigurer::disable)
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//
//                .authorizeHttpRequests()
//                .requestMatchers("/error").permitAll()
//                .requestMatchers("/api/v1/categories/**").permitAll()
//                .and()
//
//                .build();
//    }
//}
