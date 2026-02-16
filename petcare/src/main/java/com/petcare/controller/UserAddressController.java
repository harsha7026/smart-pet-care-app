package com.petcare.controller;

import com.petcare.dto.UserAddressDTO;
import com.petcare.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class UserAddressController {

    @Autowired
    private UserAddressService userAddressService;

    @Autowired
    private com.petcare.repository.UserRepository userRepository;

    private Long getUserIdFromAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }

    @GetMapping("/api/users/me/address")
    public ResponseEntity<?> getMyAddress() {
        try {
            Long userId = getUserIdFromAuth();
            com.petcare.dto.UserAddressDTO dto = userAddressService.getDefaultAddressForUser(userId);
            return ResponseEntity.ok(Map.of("success", true, "address", dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/api/users/me/address")
    public ResponseEntity<?> saveMyAddress(@RequestBody UserAddressDTO dto) {
        try {
            Long userId = getUserIdFromAuth();
            UserAddressDTO saved = userAddressService.saveOrUpdateDefaultAddress(userId, dto);
            return ResponseEntity.ok(Map.of("success", true, "address", saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
