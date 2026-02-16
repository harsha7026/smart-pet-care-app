package com.petcare.controller;

import com.petcare.entity.Pet;
import com.petcare.entity.User;
import com.petcare.repository.PetRepository;
import com.petcare.repository.UserRepository;
import com.petcare.service.FileUploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/pets")
public class PetPhotoController {

    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;

    public PetPhotoController(PetRepository petRepository, UserRepository userRepository, FileUploadService fileUploadService) {
        this.petRepository = petRepository;
        this.userRepository = userRepository;
        this.fileUploadService = fileUploadService;
    }

    /**
     * Upload a photo for a pet
     * POST /api/pets/{petId}/photo
     */
    @PostMapping("/{petId}/photo")
    public ResponseEntity<?> uploadPetPhoto(@PathVariable Long petId, @RequestParam("file") MultipartFile file) {
        try {
            // Get current user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
            }

            String email = auth.getName();
            User user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                return ResponseEntity.status(404).body(Map.of("message", "User not found"));
            }

            // Get pet
            Optional<Pet> petOpt = petRepository.findById(petId);
            if (petOpt.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("message", "Pet not found"));
            }

            Pet pet = petOpt.get();

            // Verify ownership
            if (pet.getUser() == null || !pet.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body(Map.of("message", "Unauthorized to update this pet"));
            }

            // Delete old photo if exists
            if (pet.getPhotoUrl() != null && !pet.getPhotoUrl().isEmpty()) {
                try {
                    fileUploadService.deletePetPhoto(pet.getPhotoUrl());
                } catch (IOException e) {
                    System.err.println("Failed to delete old photo: " + e.getMessage());
                }
            }

            // Upload new photo
            String photoPath = fileUploadService.uploadPetPhoto(file);

            // Update pet with new photo URL
            pet.setPhotoUrl(photoPath);
            petRepository.save(pet);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Photo uploaded successfully");
            response.put("photoUrl", photoPath);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("message", "File upload failed: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Internal server error: " + e.getMessage()));
        }
    }

    /**
     * Delete a pet's photo
     * DELETE /api/pets/{petId}/photo
     */
    @DeleteMapping("/{petId}/photo")
    public ResponseEntity<?> deletePetPhoto(@PathVariable Long petId) {
        try {
            // Get current user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
            }

            String email = auth.getName();
            User user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                return ResponseEntity.status(404).body(Map.of("message", "User not found"));
            }

            // Get pet
            Optional<Pet> petOpt = petRepository.findById(petId);
            if (petOpt.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("message", "Pet not found"));
            }

            Pet pet = petOpt.get();

            // Verify ownership
            if (pet.getUser() == null || !pet.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body(Map.of("message", "Unauthorized to update this pet"));
            }

            // Delete photo file
            if (pet.getPhotoUrl() != null && !pet.getPhotoUrl().isEmpty()) {
                fileUploadService.deletePetPhoto(pet.getPhotoUrl());
                pet.setPhotoUrl(null);
                petRepository.save(pet);
            }

            return ResponseEntity.ok(Map.of("message", "Photo deleted successfully"));

        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to delete photo: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Internal server error: " + e.getMessage()));
        }
    }
}
