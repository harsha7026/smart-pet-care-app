package com.petcare.controller;

import com.petcare.entity.MedicalHistory;
import com.petcare.entity.Pet;
import com.petcare.entity.Role;
import com.petcare.entity.User;
import com.petcare.repository.MedicalHistoryRepository;
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
public class MedicalHistoryController {

    private final PetRepository petRepository;
    private final MedicalHistoryRepository medicalHistoryRepository;
    private final UserRepository userRepository;

    public MedicalHistoryController(PetRepository petRepository, MedicalHistoryRepository medicalHistoryRepository, UserRepository userRepository) {
        this.petRepository = petRepository;
        this.medicalHistoryRepository = medicalHistoryRepository;
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

    @GetMapping("/pets/{petId}/medical-history")
    public ResponseEntity<?> list(
            @PathVariable Long petId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        User user = currentUser();
        if (user == null) return ResponseEntity.status(401).build();
        Pet pet = petRepository.findById(petId).orElse(null);
        if (pet == null) return ResponseEntity.status(404).body(Map.of("message", "Pet not found"));
        if (user.getRole() == Role.PET_OWNER && !isOwnerOfPet(user, pet)) return ResponseEntity.status(403).build();

        List<MedicalHistory> items;
        if (from != null && to != null) items = medicalHistoryRepository.findByPetAndVisitDateBetweenOrderByVisitDateDesc(pet, from, to);
        else items = medicalHistoryRepository.findByPetOrderByVisitDateDesc(pet);
        return ResponseEntity.ok(items);
    }

    @PostMapping("/vet/pets/{petId}/medical-history")
    public ResponseEntity<?> create(@PathVariable Long petId, @RequestBody MedicalHistory body) {
        User user = currentUser();
        if (user == null) return ResponseEntity.status(401).build();
        if (user.getRole() != Role.VETERINARY_DOCTOR && user.getRole() != Role.ADMIN) return ResponseEntity.status(403).build();

        Pet pet = petRepository.findById(petId).orElse(null);
        if (pet == null) return ResponseEntity.status(404).body(Map.of("message", "Pet not found"));

        body.setPet(pet);
        if (body.getVisitDate() == null) body.setVisitDate(LocalDate.now());
        return ResponseEntity.ok(medicalHistoryRepository.save(body));
    }

    @PutMapping("/vet/medical-history/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody MedicalHistory body) {
        User user = currentUser();
        if (user == null) return ResponseEntity.status(401).build();
        if (user.getRole() != Role.VETERINARY_DOCTOR && user.getRole() != Role.ADMIN) return ResponseEntity.status(403).build();

        var opt = medicalHistoryRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Record not found"));
        }
        MedicalHistory existing = opt.get();
        if (body.getVisitDate() != null) existing.setVisitDate(body.getVisitDate());
        if (body.getVisitType() != null) existing.setVisitType(body.getVisitType());
        existing.setVeterinarianName(body.getVeterinarianName());
        existing.setDiagnosis(body.getDiagnosis());
        existing.setTreatment(body.getTreatment());
        existing.setPrescription(body.getPrescription());
        existing.setNotes(body.getNotes());
        existing.setFollowUpDate(body.getFollowUpDate());
        existing.setCost(body.getCost());
        return ResponseEntity.ok(medicalHistoryRepository.save(existing));
    }
}
