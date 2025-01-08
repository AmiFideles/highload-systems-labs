package ru.itmo.marketplace.service.image.configuration;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public MinioClient minioClient(
            @Value("${app.minio.url}") String url,
            @Value("${app.minio.username}") String username,
            @Value("${app.minio.password}") String password
    ) {
        return  MinioClient.builder()
                .endpoint(url)
                .credentials(username, password)
                .build();
    }

}
