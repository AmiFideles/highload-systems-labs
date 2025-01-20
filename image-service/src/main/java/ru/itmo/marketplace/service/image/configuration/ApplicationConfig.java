package ru.itmo.marketplace.service.image.configuration;

import io.minio.MinioClient;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@OpenAPIDefinition(
        security = @SecurityRequirement(name = "bearer-key")
)
@Configuration
public class ApplicationConfig {

    @Bean
    public MinioClient minioClient(
            @Value("${app.minio.url}") String url,
            @Value("${app.minio.username}") String username,
            @Value("${app.minio.password}") String password
    ) {
        return MinioClient.builder()
                .endpoint(url)
                .credentials(username, password)
                .build();
    }

    @Value("${api.server.url}")
    private String apiServerUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(new Server().url(apiServerUrl)))
                .info(new Info()
                        .title("Market Service API")
                        .description("Market Service API Specs")
                        .version("1.0.0"))
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));
    }
}
