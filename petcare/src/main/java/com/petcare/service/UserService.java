package com.petcare.service;

import com.petcare.dto.RegisterRequest;
import com.petcare.dto.RegistrationEmailDTO;
import com.petcare.entity.Role;
import com.petcare.entity.Status;
import com.petcare.entity.User;
import com.petcare.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(RegisterRequest registerRequest) {
        // Check if email already exists
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + registerRequest.getEmail());
        }

        // Encrypt password
        String encryptedPassword = passwordEncoder.encode(registerRequest.getPassword());

        // Create and save user
        User user = new User();
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPhone(registerRequest.getPhone());
        user.setPassword(encryptedPassword);
        user.setRole(registerRequest.getRole());
        user.setStatus(Status.ACTIVE);

        User savedUser = userRepository.save(user);

        // Send registration success email asynchronously
        // This happens AFTER the database transaction commits
        try {
            sendRegistrationEmail(savedUser);
        } catch (Exception e) {
            // Log error but don't fail the registration if email fails
            log.error("Failed to send registration email for user {}: {}", savedUser.getEmail(), e.getMessage());
        }

        return savedUser;
    }

    /**
     * Send registration success email with role-aware content
     */
    private void sendRegistrationEmail(User user) {
        RegistrationEmailDTO emailData = new RegistrationEmailDTO();
        emailData.setRecipientEmail(user.getEmail());
        emailData.setUserName(user.getName());
        emailData.setRoleBadge(formatRoleBadge(user.getRole()));
        emailData.setLoginUrl(frontendUrl + "/login");
        emailData.setHelpUrl(frontendUrl + "/help");
        emailData.setPrivacyUrl(frontendUrl + "/privacy");
        emailData.setTermsUrl(frontendUrl + "/terms");
        emailData.setDoctor(user.getRole() == Role.VETERINARY_DOCTOR);
        emailData.setPetOwner(user.getRole() == Role.PET_OWNER);

        emailService.sendRegistrationSuccessEmail(emailData);
    }

    /**
     * Format role as badge text
     */
    private String formatRoleBadge(Role role) {
        if (role == Role.PET_OWNER) {
            return "🐾 Pet Owner";
        } else if (role == Role.VETERINARY_DOCTOR) {
            return "🏥 Veterinary Doctor";
        } else if (role == Role.ADMIN) {
            return "⚙️ Administrator";
        }
        return role.toString();
    }

    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    }
}
