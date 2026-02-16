package com.petcare.controller;

import com.petcare.entity.User;
import com.petcare.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserInfoController {

    private final UserRepository userRepository;

    public UserInfoController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    // Phone number pattern: supports international formats
    private static final String PHONE_REGEX = "^[+]?[(]?[0-9]{1,4}[)]?[-\\s\\.]?[(]?[0-9]{1,4}[)]?[-\\s\\.]?[0-9]{1,9}$";
    private static final int MIN_NAME_LENGTH = 2;
    private static final int MAX_NAME_LENGTH = 100;

    @GetMapping("/me")
    public ResponseEntity<?> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }
        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "phone", user.getPhone(),
                "role", user.getRole().name(),
                "status", user.getStatus().name()
        ));
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, String> updates) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }
        
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("message", "User not found"));
        }

        // Explicitly ignore email field - email cannot be updated
        if (updates.containsKey("email")) {
            updates.remove("email");
        }

        // Validate and update name
        if (updates.containsKey("name")) {
            String name = updates.get("name");
            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Name cannot be empty"));
            }
            String trimmedName = name.trim();
            if (trimmedName.length() < MIN_NAME_LENGTH) {
                return ResponseEntity.badRequest().body(Map.of("message", "Name must be at least " + MIN_NAME_LENGTH + " characters"));
            }
            if (trimmedName.length() > MAX_NAME_LENGTH) {
                return ResponseEntity.badRequest().body(Map.of("message", "Name cannot exceed " + MAX_NAME_LENGTH + " characters"));
            }
            user.setName(trimmedName);
        }

        // Validate and update phone
        if (updates.containsKey("phone")) {
            String phone = updates.get("phone");
            if (phone == null || phone.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Phone number cannot be empty"));
            }
            String trimmedPhone = phone.trim();
            if (!trimmedPhone.matches(PHONE_REGEX)) {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid phone number format"));
            }
            user.setPhone(trimmedPhone);
        }

        // Save updated user
        try {
            userRepository.save(user);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to update profile"));
        }

        return ResponseEntity.ok(Map.of(
                "message", "Profile updated successfully",
                "user", Map.of(
                        "id", user.getId(),
                        "name", user.getName(),
                        "email", user.getEmail(),
                        "phone", user.getPhone(),
                        "role", user.getRole().name()
                )
        ));
    }
}
