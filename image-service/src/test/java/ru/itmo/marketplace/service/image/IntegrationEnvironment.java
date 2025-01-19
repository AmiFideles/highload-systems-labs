package ru.itmo.marketplace.service.image;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class IntegrationEnvironment {

    static final GenericContainer<?> MINIO_CONTAINER = new GenericContainer<>(DockerImageName.parse("bitnami/minio:latest"))
            .withEnv("MINIO_ROOT_USER", "minioadmin")
            .withEnv("MINIO_ROOT_PASSWORD", "minioadmin")
            .withExposedPorts(9000);

    static {
        MINIO_CONTAINER.start();
    }

    @DynamicPropertySource
    static void minioProperties(DynamicPropertyRegistry registry) {
        String endpoint = String.format("http://%s:%d", MINIO_CONTAINER.getHost(), MINIO_CONTAINER.getFirstMappedPort());

        registry.add("app.minio.url", () -> endpoint);
        registry.add("app.minio.username", () -> "minioadmin");
        registry.add("app.minio.password", () -> "minioadmin");
    }

}
