package org.example.saadtechstore.Service;

import lombok.extern.slf4j.Slf4j;
import org.example.saadtechstore.Exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Slf4j
public class StorageService {

    @Value("${storage.local.path:uploads}")
    private String uploadPath;

    public String uploadImage(MultipartFile file) {
        try {
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            String extension = "";
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename
                        .substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID() + extension;
            Path filePath = uploadDir.resolve(filename);
            Files.copy(file.getInputStream(), filePath);
            log.info("Image sauvegardée : {}", filename);
            return "/api/images/" + filename;
        } catch (Exception e) {
            throw new RuntimeException("Erreur upload : " + e.getMessage());
        }
    }

    public InputStream getImage(String filename) {
        try {
            Path filePath = Paths.get(uploadPath).resolve(filename);
            return new FileInputStream(filePath.toFile());
        } catch (Exception e) {
            throw new ResourceNotFoundException("Image introuvable : " + filename);
        }
    }

    public void deleteImage(String filename) {
        try {
            Path filePath = Paths.get(uploadPath).resolve(filename);
            Files.deleteIfExists(filePath);
            log.info("Image supprimée : {}", filename);
        } catch (Exception e) {
            log.error("Erreur suppression : {}", e.getMessage());
        }
    }
}