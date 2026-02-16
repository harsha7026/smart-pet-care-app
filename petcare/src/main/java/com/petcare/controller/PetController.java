package com.petcare.controller;

import com.petcare.entity.Pet;
import com.petcare.entity.User;
import com.petcare.repository.PetRepository;
import com.petcare.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    private final PetRepository petRepository;
    private final UserRepository userRepository;

    public PetController(PetRepository petRepository, UserRepository userRepository) {
        this.petRepository = petRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<?> getAllPets() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }

        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("message", "User not found"));
        }

        List<Pet> pets = petRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        List<Map<String, Object>> petList = pets.stream().map(pet -> {
            Map<String, Object> petData = new HashMap<>();
            petData.put("id", pet.getId());
            petData.put("name", pet.getName());
            petData.put("species", pet.getSpecies());
            petData.put("breed", pet.getBreed());
            petData.put("age", pet.getAge());
            petData.put("gender", pet.getGender());
            petData.put("color", pet.getColor());
            petData.put("weight", pet.getWeight());
            petData.put("medicalNotes", pet.getMedicalNotes());
            petData.put("createdAt", pet.getCreatedAt());
            return petData;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(petList);
    }

    @PostMapping
    public ResponseEntity<?> addPet(@RequestBody Map<String, Object> petData) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }

        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("message", "User not found"));
        }

        // Validate mandatory fields
        String name = (String) petData.get("name");
        String species = (String) petData.get("species");

        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Pet name is required"));
        }
        if (species == null || species.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Species is required"));
        }

        // Enforce allowed species values
        var allowedSpecies = java.util.Set.of("Dog", "Cat", "Bird", "Rabbit", "Fish", "Hamster", "Other");
        if (!allowedSpecies.contains(species.trim())) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Invalid species. Allowed: Dog, Cat, Bird, Rabbit, Fish, Hamster, Other"
            ));
        }

        // Build pet entity
        Pet pet = new Pet();
        pet.setUser(user);
        pet.setName(name.trim());
        pet.setSpecies(species.trim());
        pet.setBreed(petData.get("breed") != null ? ((String) petData.get("breed")).trim() : null);
        pet.setAge(petData.get("age") != null ? ((Number) petData.get("age")).intValue() : null);
        pet.setGender(petData.get("gender") != null ? ((String) petData.get("gender")).trim() : null);
        pet.setColor(petData.get("color") != null ? ((String) petData.get("color")).trim() : null);
        pet.setWeight(petData.get("weight") != null ? ((Number) petData.get("weight")).doubleValue() : null);
        pet.setMedicalNotes(petData.get("medicalNotes") != null ? ((String) petData.get("medicalNotes")).trim() : null);

        try {
            petRepository.save(pet);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to add pet"));
        }

        return ResponseEntity.status(201).body(Map.of(
                "message", "Pet added successfully",
                "pet", Map.of(
                        "id", pet.getId(),
                        "name", pet.getName(),
                        "species", pet.getSpecies()
                )
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPetById(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }

        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("message", "User not found"));
        }

        Pet pet = petRepository.findByIdAndUserId(id, user.getId()).orElse(null);
        if (pet == null) {
            return ResponseEntity.status(404).body(Map.of("message", "Pet not found"));
        }

        Map<String, Object> petData = new HashMap<>();
        petData.put("id", pet.getId());
        petData.put("name", pet.getName());
        petData.put("species", pet.getSpecies());
        petData.put("breed", pet.getBreed());
        petData.put("age", pet.getAge());
        petData.put("gender", pet.getGender());
        petData.put("color", pet.getColor());
        petData.put("weight", pet.getWeight());
        petData.put("medicalNotes", pet.getMedicalNotes());
        petData.put("createdAt", pet.getCreatedAt());

        return ResponseEntity.ok(petData);
    }
}
