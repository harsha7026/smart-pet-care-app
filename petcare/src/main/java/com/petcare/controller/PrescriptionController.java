package com.petcare.controller;

import com.petcare.dto.PrescriptionRequest;
import com.petcare.dto.PrescriptionResponse;
import com.petcare.entity.Role;
import com.petcare.entity.User;
import com.petcare.repository.UserRepository;
import com.petcare.service.PrescriptionService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/prescriptions")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private UserRepository userRepository;

    // Doctor: Add prescription for completed appointment
    @PostMapping
    public ResponseEntity<?> createPrescription(
            @RequestBody PrescriptionRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not authenticated"));
            }

            User doctor = userRepository.findByEmail(auth.getName()).orElse(null);
            if (doctor == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not authenticated"));
            }

            if (doctor.getRole() != Role.VETERINARY_DOCTOR) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Doctor role required"));
            }

            PrescriptionResponse response = prescriptionService.createPrescription(doctor.getId(), request);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Get prescription by appointment ID (accessible by doctor and pet owner)
    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<?> getPrescriptionByAppointment(
            @PathVariable Long appointmentId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not authenticated"));
            }

            User user = userRepository.findByEmail(auth.getName()).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not authenticated"));
            }

            if (user.getRole() != Role.VETERINARY_DOCTOR && user.getRole() != Role.PET_OWNER) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Access denied"));
            }

            PrescriptionResponse response = prescriptionService.getPrescriptionByAppointmentId(appointmentId);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
