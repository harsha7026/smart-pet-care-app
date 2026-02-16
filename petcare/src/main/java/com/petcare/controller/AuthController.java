package com.petcare.controller;

import com.petcare.dto.RegisterRequest;
import com.petcare.entity.User;
import com.petcare.entity.DoctorInvitation;
import com.petcare.repository.DoctorInvitationRepository;
import com.petcare.security.JwtUtil;
import com.petcare.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AuthController {

    private final UserService userService;
    private final DoctorInvitationRepository doctorInvitationRepository;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtUtil jwtUtil;

    public AuthController(UserService userService, DoctorInvitationRepository doctorInvitationRepository) {
        this.userService = userService;
        this.doctorInvitationRepository = doctorInvitationRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            // Block direct doctor registration - doctors must use invitation link
            if (registerRequest.getRole() != null && 
                registerRequest.getRole().toString().equals("VETERINARY_DOCTOR")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "error", "Veterinary doctors cannot register directly. Please use the invitation link provided by the administrator."
                ));
            }

            User user = userService.registerUser(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "User registered successfully",
                    "userId", user.getId(),
                    "email", user.getEmail(),
                    "role", user.getRole()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Registration failed: " + e.getMessage()
            ));
        }
    }

    // Validate invitation token
    @GetMapping("/invitation/validate")
    public ResponseEntity<?> validateInvitation(@RequestParam String token) {
        try {
            Optional<DoctorInvitation> invitation = doctorInvitationRepository.findByInvitationToken(token);
            
            if (invitation.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "valid", false,
                    "error", "Invalid invitation token"
                ));
            }

            DoctorInvitation inv = invitation.get();

            if (!inv.isValid()) {
                String reason = inv.isExpired() ? "expired" : "already used or cancelled";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "valid", false,
                    "error", "This invitation has " + reason
                ));
            }

            return ResponseEntity.ok(Map.of(
                "valid", true,
                "email", inv.getEmail(),
                "expiresAt", inv.getExpiresAt()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "valid", false,
                "error", "Failed to validate invitation"
            ));
        }
    }

    // Register doctor with invitation token
    @PostMapping("/register/doctor")
    public ResponseEntity<?> registerDoctor(@RequestBody RegisterRequest registerRequest) {
        try {
            // Validate invitation token
            if (registerRequest.getInvitationToken() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", "Invitation token is required"
                ));
            }

            Optional<DoctorInvitation> invitation = doctorInvitationRepository.findByInvitationToken(
                registerRequest.getInvitationToken()
            );

            if (invitation.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "error", "Invalid invitation token"
                ));
            }

            DoctorInvitation inv = invitation.get();

            if (!inv.isValid()) {
                String reason = inv.isExpired() ? "expired" : "already used or cancelled";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", "This invitation has " + reason
                ));
            }

            // Verify email matches invitation
            if (!inv.getEmail().equalsIgnoreCase(registerRequest.getEmail())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", "Email does not match the invitation"
                ));
            }

            // Set role to VETERINARY_DOCTOR
            registerRequest.setRole(com.petcare.entity.Role.VETERINARY_DOCTOR);

            // Register the doctor
            User doctor = userService.registerUser(registerRequest);

            // Mark invitation as accepted
            inv.setStatus(DoctorInvitation.InvitationStatus.ACCEPTED);
            inv.setUsedAt(LocalDateTime.now());
            inv.setRegisteredDoctor(doctor);
            doctorInvitationRepository.save(inv);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "Doctor registered successfully. Your account is pending admin approval.",
                    "userId", doctor.getId(),
                    "email", doctor.getEmail(),
                    "role", doctor.getRole(),
                    "status", doctor.getStatus()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", e.getMessage()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Registration failed: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        try {
            String username = loginRequest.get("username");
            String password = loginRequest.get("password");

            // Get user details first to check status
            User user = userService.getUserByEmail(username);
            
            // Check if doctor account is verified/approved
            if (user.getRole().toString().equals("VETERINARY_DOCTOR")) {
                if (!user.getIsVerified() || user.getStatus().toString().equals("INACTIVE")) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                        "error", "Your account is pending admin approval. You will receive an email once approved."
                    ));
                }
            }

            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            
            // Generate JWT token
            String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getId());

            return ResponseEntity.ok(Map.of(
                    "message", "Login successful",
                    "token", token,
                    "userId", user.getId(),
                    "email", user.getEmail(),
                    "name", user.getName(),
                    "role", user.getRole().name()
            ));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "error", "Invalid username or password"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Login failed: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // With JWT, logout is handled on the client side by removing the token
        return ResponseEntity.ok(Map.of(
                "message", "Logout successful"
        ));
    }
}
