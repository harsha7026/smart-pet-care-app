package com.petcare.controller;

import com.petcare.entity.Pet;
import com.petcare.entity.User;
import com.petcare.repository.PetRepository;
import com.petcare.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pet-owner/pets")
public class PetOwnerController {

    private final PetRepository petRepository;
    private final UserRepository userRepository;

    public PetOwnerController(PetRepository petRepository, UserRepository userRepository) {
        this.petRepository = petRepository;
        this.userRepository = userRepository;
    }

    private static final Set<String> ALLOWED_SPECIES = Set.of("Dog", "Cat", "Bird", "Rabbit", "Fish", "Hamster", "Other");
    private static final int MIN_PET_NAME_LENGTH = 1;
    private static final int MAX_PET_NAME_LENGTH = 50;

    private User getLoggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        String email = auth.getName();
        return userRepository.findByEmail(email).orElse(null);
    }

    @PostMapping
    public ResponseEntity<?> addPet(@RequestBody Map<String, Object> body) {
        User user = getLoggedInUser();
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }

        String name = (String) body.get("name");
        String species = (String) body.get("species");

        // Validate pet name
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Pet name is required and cannot be empty"));
        }
        String trimmedName = name.trim();
        if (trimmedName.length() < MIN_PET_NAME_LENGTH) {
            return ResponseEntity.badRequest().body(Map.of("message", "Pet name must be at least " + MIN_PET_NAME_LENGTH + " character"));
        }
        if (trimmedName.length() > MAX_PET_NAME_LENGTH) {
            return ResponseEntity.badRequest().body(Map.of("message", "Pet name cannot exceed " + MAX_PET_NAME_LENGTH + " characters"));
        }
        
        // Validate species
        if (species == null || species.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Species is required and cannot be empty"));
        }
        species = species.trim();
        if (!ALLOWED_SPECIES.contains(species)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid species. Allowed: Dog, Cat, Bird, Rabbit, Fish, Hamster, Other"));
        }

        Pet pet = new Pet();
        pet.setUser(user);
        pet.setName(name.trim());
        pet.setSpecies(species);
        pet.setBreed(body.get("breed") != null ? ((String) body.get("breed")).trim() : null);
        pet.setAge(body.get("age") != null ? ((Number) body.get("age")).intValue() : null);
        pet.setGender(body.get("gender") != null ? ((String) body.get("gender")).trim() : null);
        pet.setColor(body.get("color") != null ? ((String) body.get("color")).trim() : null);
        pet.setWeight(body.get("weight") != null ? ((Number) body.get("weight")).doubleValue() : null);
        pet.setMedicalNotes(body.get("medicalNotes") != null ? ((String) body.get("medicalNotes")).trim() : null);

        try {
            Pet savedPet = petRepository.save(pet);
            System.out.println("Pet saved successfully: ID=" + savedPet.getId() + ", Name=" + savedPet.getName() + ", User=" + user.getId());
            return ResponseEntity.status(201).body(Map.of(
                    "message", "Pet added successfully",
                    "id", savedPet.getId(),
                    "name", savedPet.getName()
            ));
        } catch (Exception e) {
            System.err.println("Error saving pet: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Failed to add pet: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> listPets() {
        User user = getLoggedInUser();
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }

        List<Pet> pets = petRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        System.out.println("Fetching pets for user: " + user.getId() + ", Found: " + pets.size() + " pets");
        
        List<Map<String, Object>> response = pets.stream().map(pet -> {
            Map<String, Object> petMap = new LinkedHashMap<>();
            petMap.put("id", pet.getId());
            petMap.put("name", pet.getName());
            petMap.put("species", pet.getSpecies());
            petMap.put("breed", pet.getBreed());
            petMap.put("age", pet.getAge());
            petMap.put("gender", pet.getGender());
            petMap.put("color", pet.getColor());
            petMap.put("weight", pet.getWeight());
            petMap.put("medicalNotes", pet.getMedicalNotes());
            petMap.put("createdAt", pet.getCreatedAt());
            return petMap;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPet(@PathVariable Long id) {
        User user = getLoggedInUser();
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }

        Pet pet = petRepository.findByIdAndUserId(id, user.getId()).orElse(null);
        if (pet == null) {
            return ResponseEntity.status(404).body(Map.of("message", "Pet not found"));
        }

        Map<String, Object> petMap = new LinkedHashMap<>();
        petMap.put("id", pet.getId());
        petMap.put("name", pet.getName());
        petMap.put("species", pet.getSpecies());
        petMap.put("breed", pet.getBreed());
        petMap.put("age", pet.getAge());
        petMap.put("gender", pet.getGender());
        petMap.put("color", pet.getColor());
        petMap.put("weight", pet.getWeight());
        petMap.put("medicalNotes", pet.getMedicalNotes());
        petMap.put("createdAt", pet.getCreatedAt());
        petMap.put("updatedAt", pet.getUpdatedAt());
        return ResponseEntity.ok(petMap);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePet(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        User user = getLoggedInUser();
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }

        Pet pet = petRepository.findByIdAndUserId(id, user.getId()).orElse(null);
        if (pet == null) {
            return ResponseEntity.status(404).body(Map.of("message", "Pet not found"));
        }

        // Update fields only if present
        if (body.containsKey("name")) {
            String name = (String) body.get("name");
            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Pet name cannot be empty"));
            }
            String trimmedName = name.trim();
            if (trimmedName.length() < MIN_PET_NAME_LENGTH) {
                return ResponseEntity.badRequest().body(Map.of("message", "Pet name must be at least " + MIN_PET_NAME_LENGTH + " character"));
            }
            if (trimmedName.length() > MAX_PET_NAME_LENGTH) {
                return ResponseEntity.badRequest().body(Map.of("message", "Pet name cannot exceed " + MAX_PET_NAME_LENGTH + " characters"));
            }
            pet.setName(trimmedName);
        }
        if (body.containsKey("species")) {
            String species = (String) body.get("species");
            if (species == null || species.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Species cannot be empty"));
            }
            species = species.trim();
            if (!ALLOWED_SPECIES.contains(species)) {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid species. Allowed: Dog, Cat, Bird, Rabbit, Fish, Hamster, Other"));
            }
            pet.setSpecies(species);
        }
        if (body.containsKey("breed")) {
            pet.setBreed(body.get("breed") != null ? ((String) body.get("breed")).trim() : null);
        }
        if (body.containsKey("age")) {
            pet.setAge(body.get("age") != null ? ((Number) body.get("age")).intValue() : null);
        }
        if (body.containsKey("gender")) {
            pet.setGender(body.get("gender") != null ? ((String) body.get("gender")).trim() : null);
        }
        if (body.containsKey("color")) {
            pet.setColor(body.get("color") != null ? ((String) body.get("color")).trim() : null);
        }
        if (body.containsKey("weight")) {
            pet.setWeight(body.get("weight") != null ? ((Number) body.get("weight")).doubleValue() : null);
        }
        if (body.containsKey("medicalNotes")) {
            pet.setMedicalNotes(body.get("medicalNotes") != null ? ((String) body.get("medicalNotes")).trim() : null);
        }

        try {
            petRepository.save(pet);
            return ResponseEntity.ok(Map.of("message", "Pet updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to update pet"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePet(@PathVariable Long id) {
        User user = getLoggedInUser();
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }

        Pet pet = petRepository.findByIdAndUserId(id, user.getId()).orElse(null);
        if (pet == null) {
            return ResponseEntity.status(404).body(Map.of("message", "Pet not found"));
        }

        try {
            petRepository.delete(pet);
            return ResponseEntity.ok(Map.of("message", "Pet deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to delete pet"));
        }
    }
}
