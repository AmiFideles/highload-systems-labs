package ru.itmo.marketplace.service.authentication.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@OpenAPIDefinition
@Configuration
public class ApplicationConfig {

    @Value("${api.server.url}")
    private String apiServerUrl;


    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(new Server().url(apiServerUrl)))
                .info(new Info()
                        .title("Authentication service API")
                        .description("Authentication Service API Specs")
                        .version("1.0.0"));
    }
}
