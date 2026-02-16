package com.petcare.controller;

import com.petcare.entity.HealthReminder;
import com.petcare.entity.Pet;
import com.petcare.entity.Role;
import com.petcare.entity.User;
import com.petcare.repository.HealthReminderRepository;
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
public class HealthRemindersController {

    private final PetRepository petRepository;
    private final HealthReminderRepository healthReminderRepository;
    private final UserRepository userRepository;

    public HealthRemindersController(PetRepository petRepository, HealthReminderRepository healthReminderRepository, UserRepository userRepository) {
        this.petRepository = petRepository;
        this.healthReminderRepository = healthReminderRepository;
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

    @GetMapping("/pets/{petId}/reminders")
    public ResponseEntity<?> list(
            @PathVariable Long petId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        User user = currentUser();
        if (user == null) return ResponseEntity.status(401).build();
        Pet pet = petRepository.findById(petId).orElse(null);
        if (pet == null) return ResponseEntity.status(404).body(Map.of("message", "Pet not found"));
        if (user.getRole() == Role.PET_OWNER && !isOwnerOfPet(user, pet)) return ResponseEntity.status(403).build();

        List<HealthReminder> items;
        if (from != null && to != null) items = healthReminderRepository.findByPetAndReminderDateBetweenOrderByReminderDateAsc(pet, from, to);
        else items = healthReminderRepository.findByPetOrderByReminderDateAsc(pet);
        return ResponseEntity.ok(items);
    }

    // Owner can create CHECKUP or CUSTOM; Vet can create any including VACCINATION
    @PostMapping("/pets/{petId}/reminders")
    public ResponseEntity<?> create(@PathVariable Long petId, @RequestBody HealthReminder body) {
        User user = currentUser();
        if (user == null) return ResponseEntity.status(401).build();

        Pet pet = petRepository.findById(petId).orElse(null);
        if (pet == null) return ResponseEntity.status(404).body(Map.of("message", "Pet not found"));

        if (user.getRole() == Role.PET_OWNER && !isOwnerOfPet(user, pet)) return ResponseEntity.status(403).build();

        if (user.getRole() == Role.PET_OWNER) {
            // Restrict owner types
            String type = body.getReminderType();
            if (type == null || !(type.equals("CHECKUP") || type.equals("CUSTOM"))) {
                return ResponseEntity.badRequest().body(Map.of("message", "Owners can create only CHECKUP or CUSTOM reminders"));
            }
        }

        body.setPet(pet);
        if (body.getStatus() == null) body.setStatus("PENDING");
        body.setNotificationSent(Boolean.FALSE);
        return ResponseEntity.ok(healthReminderRepository.save(body));
    }

    // Update reminder status; owners limited to their pets only
    @PutMapping("/reminders/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        User user = currentUser();
        if (user == null) return ResponseEntity.status(401).build();

        return healthReminderRepository.findById(id)
                .map(existing -> {
                    if (user.getRole() == Role.PET_OWNER && !isOwnerOfPet(user, existing.getPet())) {
                        return ResponseEntity.status(403).build();
                    }
                    String status = payload.get("status");
                    if (status != null) existing.setStatus(status);
                    return ResponseEntity.ok(healthReminderRepository.save(existing));
                })
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "Reminder not found")));
    }
}
