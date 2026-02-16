package com.petcare.controller;

import com.petcare.entity.HealthMetric;
import com.petcare.entity.Pet;
import com.petcare.entity.Role;
import com.petcare.entity.User;
import com.petcare.repository.HealthMetricRepository;
import com.petcare.repository.PetRepository;
import com.petcare.repository.UserRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthMetricsController {

    private final PetRepository petRepository;
    private final HealthMetricRepository healthMetricRepository;
    private final UserRepository userRepository;

    public HealthMetricsController(PetRepository petRepository, HealthMetricRepository healthMetricRepository, UserRepository userRepository) {
        this.petRepository = petRepository;
        this.healthMetricRepository = healthMetricRepository;
        this.userRepository = userRepository;
    }

    private User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        return userRepository.findByEmail(auth.getName()).orElse(null);
    }

    private boolean isOwnerOfPet(User user, Pet pet) {
        return user != null && pet != null && pet.getUser() != null && pet.getUser().getId().equals(user.getId());
    }

    // List health metrics (owner can only view own pets; vet can view any for now)
    @GetMapping("/pets/{petId}/health/metrics")
    public ResponseEntity<?> listMetrics(
            @PathVariable Long petId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        User user = currentUser();
        if (user == null) return ResponseEntity.status(401).build();

        Pet pet = petRepository.findById(petId).orElse(null);
        if (pet == null) return ResponseEntity.status(404).body(Map.of("message", "Pet not found"));

        if (user.getRole() == Role.PET_OWNER && !isOwnerOfPet(user, pet)) {
            return ResponseEntity.status(403).body(Map.of("message", "Forbidden"));
        }

        List<HealthMetric> items;
        if (from != null && to != null) {
            items = healthMetricRepository.findByPetAndRecordDateBetweenOrderByRecordDateAsc(pet, from, to);
        } else {
            items = healthMetricRepository.findByPetOrderByRecordDateDesc(pet);
        }
        return ResponseEntity.ok(items);
    }

    // Owner can add their own daily metrics but cannot set vet-only fields
    @PostMapping("/owner/pets/{petId}/health/metrics")
    public ResponseEntity<?> addOwnerMetric(@PathVariable Long petId, @RequestBody HealthMetric body) {
        User user = currentUser();
        if (user == null) return ResponseEntity.status(401).build();
        if (user.getRole() != Role.PET_OWNER) return ResponseEntity.status(403).build();

        Pet pet = petRepository.findById(petId).orElse(null);
        if (pet == null) return ResponseEntity.status(404).body(Map.of("message", "Pet not found"));
        if (!isOwnerOfPet(user, pet)) return ResponseEntity.status(403).build();

        HealthMetric m = new HealthMetric();
        m.setPet(pet);
        m.setRecordDate(body.getRecordDate() != null ? body.getRecordDate() : LocalDate.now());
        m.setWeight(body.getWeight());
        m.setCalorieIntake(body.getCalorieIntake());
        m.setActivityLevel(body.getActivityLevel());
        m.setStressLevel(body.getStressLevel());
        // Owner cannot set blood pressure or mark vetEntered
        m.setBloodPressure(null);
        m.setVetEntered(Boolean.FALSE);
        m.setNotes(body.getNotes());

        return ResponseEntity.ok(healthMetricRepository.save(m));
    }

    // Vet: add or update full metric including blood pressure
    @PostMapping("/vet/pets/{petId}/health/metrics")
    public ResponseEntity<?> addVetMetric(@PathVariable Long petId, @RequestBody HealthMetric body) {
        User user = currentUser();
        if (user == null) return ResponseEntity.status(401).build();
        if (user.getRole() != Role.VETERINARY_DOCTOR && user.getRole() != Role.ADMIN)
            return ResponseEntity.status(403).build();

        Pet pet = petRepository.findById(petId).orElse(null);
        if (pet == null) return ResponseEntity.status(404).body(Map.of("message", "Pet not found"));

        HealthMetric m = new HealthMetric();
        m.setPet(pet);
        m.setRecordDate(body.getRecordDate() != null ? body.getRecordDate() : LocalDate.now());
        m.setWeight(body.getWeight());
        m.setCalorieIntake(body.getCalorieIntake());
        m.setActivityLevel(body.getActivityLevel());
        m.setStressLevel(body.getStressLevel());
        m.setBloodPressure(body.getBloodPressure());
        m.setVetEntered(Boolean.TRUE);
        m.setNotes(body.getNotes());

        return ResponseEntity.ok(healthMetricRepository.save(m));
    }

    @PutMapping("/vet/health/metrics/{id}")
    public ResponseEntity<?> updateVetMetric(@PathVariable Long id, @RequestBody HealthMetric body) {
        User user = currentUser();
        if (user == null) return ResponseEntity.status(401).build();
        if (user.getRole() != Role.VETERINARY_DOCTOR && user.getRole() != Role.ADMIN)
            return ResponseEntity.status(403).build();

        var opt = healthMetricRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Metric not found"));
        }
        HealthMetric existing = opt.get();
        existing.setRecordDate(body.getRecordDate() != null ? body.getRecordDate() : existing.getRecordDate());
        existing.setWeight(body.getWeight());
        existing.setCalorieIntake(body.getCalorieIntake());
        existing.setActivityLevel(body.getActivityLevel());
        existing.setStressLevel(body.getStressLevel());
        existing.setBloodPressure(body.getBloodPressure());
        existing.setNotes(body.getNotes());
        existing.setVetEntered(Boolean.TRUE);
        return ResponseEntity.ok(healthMetricRepository.save(existing));
    }
}
