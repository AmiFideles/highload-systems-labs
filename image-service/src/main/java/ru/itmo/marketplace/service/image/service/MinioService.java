package ru.itmo.marketplace.service.image.service;

import io.minio.*;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.itmo.common.exception.NotFoundException;

import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {
    private final MinioClient minioClient;

    @Value("${app.minio.bucket}")
    private String bucketName;

    public Optional<String> upload(String filename, InputStream inputStream, String contentType) {
        try {
            String objectName = UUID.randomUUID() + "." + filename;
            ensureBucketExists();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, -1, 10485760)
                            .contentType(contentType)
                            .build());
            return Optional.of(objectName);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    public InputStream download(String objectName) {
        try {
            ensureBucketExists();
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build()
            );
        } catch (ErrorResponseException e) {
            log.error(e.errorResponse().code());
            if (e.errorResponse().code().equals("NoSuchKey")) {
                throw new NotFoundException("Image not found with name=%s".formatted(objectName));
            }
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getFileContentType(String objectName) {
        try {
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
            return stat.contentType();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void delete(String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
        } catch (ErrorResponseException e) {
            log.error(e.errorResponse().code());
            if (e.errorResponse().code().equals("NoSuchKey")) {
                throw new NotFoundException("Image not found with name=%s".formatted(objectName));
            }
            throw new RuntimeException(e);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void ensureBucketExists() throws Exception {
        boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!isExist) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

}
