package org.example.saadtechstore.Service;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.saadtechstore.Exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    public String uploadImage(MultipartFile file) {
        try {
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(filename)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
            return "/api/images/" + filename;
        } catch (Exception e) {
            throw new RuntimeException("Erreur upload image : " + e.getMessage());
        }
    }

    public InputStream getImage(String filename) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket).object(filename).build());
        } catch (Exception e) {
            throw new ResourceNotFoundException("Image introuvable");
        }
    }
}