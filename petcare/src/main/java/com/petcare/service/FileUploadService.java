package com.petcare.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileUploadService {

    @Value("${file.upload.dir:uploads}")
    private String uploadDir;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_EXTENSIONS = {"jpg", "jpeg", "png", "gif"};

    /**
     * Upload a pet photo and return the file path
     */
    public String uploadPetPhoto(MultipartFile file) throws IOException {
        // Validate file
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds 5MB limit");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !isAllowedExtension(filename)) {
            throw new IllegalArgumentException("Invalid file type. Only JPG, PNG, and GIF are allowed");
        }

        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir, "pet-photos");
        Files.createDirectories(uploadPath);

        // Generate unique filename
        String extension = filename.substring(filename.lastIndexOf("."));
        String uniqueFilename = UUID.randomUUID() + extension;
        Path filePath = uploadPath.resolve(uniqueFilename);

        // Save file
        Files.copy(file.getInputStream(), filePath);

        // Return the relative path for storage in database
        return "pet-photos/" + uniqueFilename;
    }

    /**
     * Delete a pet photo by path
     */
    public void deletePetPhoto(String photoPath) throws IOException {
        if (photoPath == null || photoPath.isEmpty()) {
            return;
        }

        Path filePath = Paths.get(uploadDir, photoPath);
        Files.deleteIfExists(filePath);
    }

    /**
     * Check if file extension is allowed
     */
    private boolean isAllowedExtension(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        for (String allowed : ALLOWED_EXTENSIONS) {
            if (extension.equals(allowed)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the full file path for serving
     */
    public Path getFilePath(String relativePhotoPath) {
        return Paths.get(uploadDir, relativePhotoPath);
    }
}
