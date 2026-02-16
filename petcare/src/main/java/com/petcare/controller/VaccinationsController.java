package com.petcare.controller;

import com.petcare.entity.Pet;
import com.petcare.entity.Role;
import com.petcare.entity.User;
import com.petcare.entity.VaccinationRecord;
import com.petcare.repository.PetRepository;
import com.petcare.repository.UserRepository;
import com.petcare.repository.VaccinationRecordRepository;
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
public class VaccinationsController {

    private final PetRepository petRepository;
    private final VaccinationRecordRepository vaccinationRecordRepository;
    private final UserRepository userRepository;

    public VaccinationsController(PetRepository petRepository, VaccinationRecordRepository vaccinationRecordRepository, UserRepository userRepository) {
        this.petRepository = petRepository;
        this.vaccinationRecordRepository = vaccinationRecordRepository;
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

    private String computeStatus(VaccinationRecord rec) {
        LocalDate today = LocalDate.now();
        if (rec.getAdministeredDate() != null) return "COMPLETED";
        LocalDate due = rec.getDueDate() != null ? rec.getDueDate() : rec.getNextDueDate();
        if (due == null) return rec.getStatus() != null ? rec.getStatus() : "SCHEDULED";
        if (due.isBefore(today)) return "OVERDUE";
        if (due.isEqual(today) || due.isAfter(today)) return "DUE";
        return "SCHEDULED";
    }

    @GetMapping("/pets/{petId}/vaccinations")
    public ResponseEntity<?> list(
            @PathVariable Long petId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        User user = currentUser();
        if (user == null) return ResponseEntity.status(401).build();
        Pet pet = petRepository.findById(petId).orElse(null);
        if (pet == null) return ResponseEntity.status(404).body(Map.of("message", "Pet not found"));
        if (user.getRole() == Role.PET_OWNER && !isOwnerOfPet(user, pet)) return ResponseEntity.status(403).build();

        List<VaccinationRecord> items;
        if (from != null && to != null) items = vaccinationRecordRepository.findByPetAndDueDateBetweenOrderByDueDateAsc(pet, from, to);
        else items = vaccinationRecordRepository.findByPetOrderByDueDateAsc(pet);
        return ResponseEntity.ok(items);
    }

    @PostMapping("/vet/pets/{petId}/vaccinations")
    public ResponseEntity<?> create(@PathVariable Long petId, @RequestBody VaccinationRecord body) {
        User user = currentUser();
        if (user == null) return ResponseEntity.status(401).build();
        if (user.getRole() != Role.VETERINARY_DOCTOR && user.getRole() != Role.ADMIN) return ResponseEntity.status(403).build();

        Pet pet = petRepository.findById(petId).orElse(null);
        if (pet == null) return ResponseEntity.status(404).body(Map.of("message", "Pet not found"));

        VaccinationRecord rec = new VaccinationRecord();
        rec.setPet(pet);
        rec.setVaccineName(body.getVaccineName());
        rec.setAdministeredDate(body.getAdministeredDate());
        rec.setDueDate(body.getDueDate());
        rec.setNextDueDate(body.getNextDueDate());
        rec.setVeterinarianName(body.getVeterinarianName());
        rec.setBatchNumber(body.getBatchNumber());
        rec.setNotes(body.getNotes());
        rec.setStatus(computeStatus(body));
        return ResponseEntity.ok(vaccinationRecordRepository.save(rec));
    }

    @PutMapping("/vet/vaccinations/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody VaccinationRecord body) {
        User user = currentUser();
        if (user == null) return ResponseEntity.status(401).build();
        if (user.getRole() != Role.VETERINARY_DOCTOR && user.getRole() != Role.ADMIN) return ResponseEntity.status(403).build();

        var opt = vaccinationRecordRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Record not found"));
        }
        VaccinationRecord existing = opt.get();
        if (body.getVaccineName() != null) existing.setVaccineName(body.getVaccineName());
        existing.setAdministeredDate(body.getAdministeredDate());
        existing.setDueDate(body.getDueDate());
        existing.setNextDueDate(body.getNextDueDate());
        existing.setVeterinarianName(body.getVeterinarianName());
        existing.setBatchNumber(body.getBatchNumber());
        existing.setNotes(body.getNotes());
        existing.setStatus(computeStatus(existing));
        return ResponseEntity.ok(vaccinationRecordRepository.save(existing));
    }
}
