package ru.itmo.marketplace.service.image.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import ru.itmo.common.exception.NotFoundException;
import ru.itmo.marketplace.service.image.service.MinioService;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class ImagesController {
    private final MinioService minioService;

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/{filename}",
            produces = {"application/json"}
    )
    public ResponseEntity<InputStreamResource> downloadImage(
            @PathVariable("filename") String filename
    ) {
        InputStream inputStream = minioService.download(filename);
        InputStreamResource resource = new InputStreamResource(inputStream);
        String contentType = minioService.getFileContentType(filename);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "",
            produces = {"application/json"},
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<String> uploadImage(
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        return minioService.upload(
                        file.getOriginalFilename(),
                        file.getInputStream(),
                        file.getContentType()
                )
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/{filename}",
            produces = {"application/json"}
    )
    public ResponseEntity<?> delete(
            @PathVariable("filename") String filename
    ) {
        minioService.delete(filename);
        return ResponseEntity.ok().build();
    }

}
