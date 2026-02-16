package com.petcare.controller;

import com.petcare.entity.Pet;
import com.petcare.entity.Role;
import com.petcare.entity.Status;
import com.petcare.entity.User;
import com.petcare.entity.DoctorInvitation;
import com.petcare.repository.PetRepository;
import com.petcare.repository.UserRepository;
import com.petcare.repository.AppointmentRepository;
import com.petcare.repository.OrderRepository;
import com.petcare.repository.OrderItemRepository;
import com.petcare.repository.DoctorInvitationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final AppointmentRepository appointmentRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final DoctorInvitationRepository doctorInvitationRepository;

    public AdminController(UserRepository userRepository, PetRepository petRepository, 
                          AppointmentRepository appointmentRepository, OrderRepository orderRepository,
                          OrderItemRepository orderItemRepository, DoctorInvitationRepository doctorInvitationRepository) {
        this.userRepository = userRepository;
        this.petRepository = petRepository;
        this.appointmentRepository = appointmentRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.doctorInvitationRepository = doctorInvitationRepository;
    }

    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        String email = auth.getName();
        Optional<User> user = userRepository.findByEmail(email);
        return user.isPresent() && user.get().getRole() == Role.ADMIN;
    }

    private ResponseEntity<?> forbiddenResponse() {
        return ResponseEntity.status(403).body(Map.of("message", "Access Denied: Admin role required"));
    }

    // ===== DOCTOR INVITATION SYSTEM =====
    @PostMapping("/invitations/doctor")
    public ResponseEntity<?> inviteDoctor(@RequestBody Map<String, String> body) {
        if (!isAdmin()) {
            return forbiddenResponse();
        }

        try {
            String email = body.get("email");
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
            }

            // Check if user already exists
            if (userRepository.findByEmail(email).isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("error", "User with this email already exists"));
            }

            // Check if there's already a pending invitation
            Optional<DoctorInvitation> existingInvitation = doctorInvitationRepository.findByEmail(email);
            if (existingInvitation.isPresent() && existingInvitation.get().isValid()) {
                return ResponseEntity.badRequest().body(Map.of("error", "An active invitation already exists for this email"));
            }

            // Get current admin
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Optional<User> admin = userRepository.findByEmail(auth.getName());

            // Create new invitation
            DoctorInvitation invitation = new DoctorInvitation();
            invitation.setEmail(email);
            invitation.setInvitationToken(UUID.randomUUID().toString());
            invitation.setCreatedBy(admin.orElse(null));

            doctorInvitationRepository.save(invitation);

            // Create invitation link
            String invitationLink = "http://localhost:3000/register-doctor?token=" + invitation.getInvitationToken();

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("message", "Invitation sent successfully");
            response.put("invitationLink", invitationLink);
            response.put("email", email);
            response.put("expiresAt", invitation.getExpiresAt());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Failed to create invitation"));
        }
    }

    @GetMapping("/invitations")
    public ResponseEntity<?> getAllInvitations() {
        if (!isAdmin()) {
            return forbiddenResponse();
        }

        try {
            List<Map<String, Object>> invitations = doctorInvitationRepository.findAll().stream()
                    .map(inv -> {
                        Map<String, Object> map = new LinkedHashMap<>();
                        map.put("id", inv.getId());
                        map.put("email", inv.getEmail());
                        map.put("status", inv.getStatus().toString());
                        map.put("createdAt", inv.getCreatedAt());
                        map.put("expiresAt", inv.getExpiresAt());
                        map.put("usedAt", inv.getUsedAt());
                        map.put("isExpired", inv.isExpired());
                        map.put("invitationLink", "http://localhost:3000/register-doctor?token=" + inv.getInvitationToken());
                        if (inv.getRegisteredDoctor() != null) {
                            map.put("doctorName", inv.getRegisteredDoctor().getName());
                            map.put("doctorId", inv.getRegisteredDoctor().getId());
                        }
                        return map;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(invitations);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch invitations"));
        }
    }

    @DeleteMapping("/invitations/{id}")
    public ResponseEntity<?> cancelInvitation(@PathVariable Long id) {
        if (!isAdmin()) {
            return forbiddenResponse();
        }

        try {
            Optional<DoctorInvitation> invitation = doctorInvitationRepository.findById(id);
            if (invitation.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("error", "Invitation not found"));
            }

            DoctorInvitation inv = invitation.get();
            if (inv.getStatus() == DoctorInvitation.InvitationStatus.ACCEPTED) {
                return ResponseEntity.badRequest().body(Map.of("error", "Cannot cancel an accepted invitation"));
            }

            inv.setStatus(DoctorInvitation.InvitationStatus.CANCELLED);
            doctorInvitationRepository.save(inv);

            return ResponseEntity.ok(Map.of("message", "Invitation cancelled successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to cancel invitation"));
        }
    }

    // ===== DASHBOARD OVERVIEW =====
    @GetMapping("/dashboard/overview")
    public ResponseEntity<?> getDashboardOverview() {
        if (!isAdmin()) {
            return forbiddenResponse();
        }

        try {
            long totalUsers = userRepository.count();
            long totalPets = petRepository.count();
            
            long petOwners = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == Role.PET_OWNER)
                    .count();
            
            long veterinaryDoctors = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == Role.VETERINARY_DOCTOR)
                    .count();
            
            long totalAppointments = appointmentRepository.count();
            
            // Calculate total revenue from completed and confirmed appointments
            java.math.BigDecimal totalRevenue = appointmentRepository.findAll().stream()
                    .filter(apt -> apt.getStatus().toString().equals("CONFIRMED") || 
                                 apt.getStatus().toString().equals("COMPLETED"))
                    .map(apt -> apt.getFee() != null ? apt.getFee() : java.math.BigDecimal.ZERO)
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

            Map<String, Object> overview = new LinkedHashMap<>();
            overview.put("totalUsers", totalUsers);
            overview.put("totalPetOwners", petOwners);
            overview.put("totalVeterinaryDoctors", veterinaryDoctors);
            overview.put("totalPets", totalPets);
            overview.put("totalAppointments", totalAppointments);
            overview.put("totalRevenue", totalRevenue.doubleValue());

            return ResponseEntity.ok(overview);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to fetch overview"));
        }
    }

    // ===== USER MANAGEMENT =====
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        if (!isAdmin()) {
            return forbiddenResponse();
        }

        try {
            List<Map<String, Object>> users = userRepository.findAll().stream().map(user -> {
                Map<String, Object> userMap = new LinkedHashMap<>();
                userMap.put("id", user.getId());
                userMap.put("name", user.getName());
                userMap.put("email", user.getEmail());
                userMap.put("phone", user.getPhone());
                userMap.put("role", user.getRole().toString());
                userMap.put("status", user.getStatus().toString());
                userMap.put("createdAt", user.getCreatedAt());
                return userMap;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to fetch users"));
        }
    }

    @PutMapping("/users/{id}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        if (!isAdmin()) {
            return forbiddenResponse();
        }

        try {
            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("message", "User not found"));
            }

            User user = userOptional.get();
            String status = body.get("status");

            if (status == null || (!status.equals("ACTIVE") && !status.equals("INACTIVE"))) {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid status"));
            }

            user.setStatus(Status.valueOf(status));
            userRepository.save(user);

            return ResponseEntity.ok(Map.of("message", "User status updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to update user status"));
        }
    }

    // ===== VETERINARY DOCTORS MANAGEMENT =====
    @GetMapping("/doctors")
    public ResponseEntity<?> getAllDoctors() {
        if (!isAdmin()) {
            return forbiddenResponse();
        }

        try {
            List<Map<String, Object>> doctors = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == Role.VETERINARY_DOCTOR)
                    .map(user -> {
                        Map<String, Object> doctorMap = new LinkedHashMap<>();
                        doctorMap.put("id", user.getId());
                        doctorMap.put("name", user.getName());
                        doctorMap.put("email", user.getEmail());
                        doctorMap.put("phone", user.getPhone());
                        doctorMap.put("specialization", user.getSpecialization());
                        doctorMap.put("licenseNumber", user.getLicenseNumber());
                        doctorMap.put("documentUrl", user.getDocumentUrl());
                        doctorMap.put("isVerified", user.getIsVerified());
                        doctorMap.put("status", user.getStatus().toString());
                        doctorMap.put("createdAt", user.getCreatedAt());
                        return doctorMap;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(doctors);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to fetch doctors"));
        }
    }

    @PutMapping("/doctors/{doctorId}/verify")
    public ResponseEntity<?> verifyDoctor(@PathVariable Long doctorId) {
        if (!isAdmin()) {
            return forbiddenResponse();
        }

        try {
            Optional<User> userOpt = userRepository.findById(doctorId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("message", "Doctor not found"));
            }

            User doctor = userOpt.get();
            if (doctor.getRole() != Role.VETERINARY_DOCTOR) {
                return ResponseEntity.status(400).body(Map.of("message", "User is not a doctor"));
            }

            doctor.setIsVerified(true);
            doctor.setStatus(Status.ACTIVE);
            userRepository.save(doctor);

            return ResponseEntity.ok(Map.of(
                "message", "Doctor verified successfully",
                "doctorId", doctorId,
                "isVerified", true
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to verify doctor"));
        }
    }

    @PutMapping("/doctors/{doctorId}/reject")
    public ResponseEntity<?> rejectDoctor(@PathVariable Long doctorId, @RequestBody(required = false) Map<String, String> payload) {
        if (!isAdmin()) {
            return forbiddenResponse();
        }

        try {
            Optional<User> userOpt = userRepository.findById(doctorId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("message", "Doctor not found"));
            }

            User doctor = userOpt.get();
            if (doctor.getRole() != Role.VETERINARY_DOCTOR) {
                return ResponseEntity.status(400).body(Map.of("message", "User is not a doctor"));
            }

            doctor.setIsVerified(false);
            doctor.setStatus(Status.INACTIVE);
            userRepository.save(doctor);

            return ResponseEntity.ok(Map.of(
                "message", "Doctor verification rejected",
                "doctorId", doctorId,
                "isVerified", false
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to reject doctor"));
        }
    }

    // ===== PETS OVERVIEW =====
    @GetMapping("/pets")
    public ResponseEntity<?> getAllPets() {
        if (!isAdmin()) {
            return forbiddenResponse();
        }

        try {
            List<Map<String, Object>> pets = petRepository.findAll().stream().map(pet -> {
                Map<String, Object> petMap = new LinkedHashMap<>();
                petMap.put("id", pet.getId());
                petMap.put("name", pet.getName());
                petMap.put("species", pet.getSpecies());
                petMap.put("breed", pet.getBreed());
                petMap.put("age", pet.getAge());
                petMap.put("ownerName", pet.getUser() != null ? pet.getUser().getName() : "Unknown");
                petMap.put("ownerEmail", pet.getUser() != null ? pet.getUser().getEmail() : "Unknown");
                petMap.put("createdAt", pet.getCreatedAt());
                return petMap;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(pets);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to fetch pets"));
        }
    }

    // ===== STATISTICS =====
    @GetMapping("/statistics")
    public ResponseEntity<?> getStatistics() {
        if (!isAdmin()) {
            return forbiddenResponse();
        }

        try {
            Map<String, Object> stats = new LinkedHashMap<>();

            // User statistics
            long totalUsers = userRepository.count();
            long activeUsers = userRepository.findAll().stream()
                    .filter(u -> u.getStatus() == Status.ACTIVE)
                    .count();
            long inactiveUsers = totalUsers - activeUsers;

            stats.put("userStatistics", Map.of(
                    "totalUsers", totalUsers,
                    "activeUsers", activeUsers,
                    "inactiveUsers", inactiveUsers
            ));

            // Role statistics
            long petOwners = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == Role.PET_OWNER)
                    .count();
            long doctors = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == Role.VETERINARY_DOCTOR)
                    .count();
            long admins = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == Role.ADMIN)
                    .count();

            stats.put("roleStatistics", Map.of(
                    "petOwners", petOwners,
                    "veterinaryDoctors", doctors,
                    "admins", admins
            ));

            // Pet statistics
            long totalPets = petRepository.count();
            Map<String, Long> petsBySpecies = petRepository.findAll().stream()
                    .collect(Collectors.groupingBy(Pet::getSpecies, Collectors.counting()));

            stats.put("petStatistics", Map.of(
                    "totalPets", totalPets,
                    "bySpecies", petsBySpecies
            ));

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to fetch statistics"));
        }
    }

    // ===== APPOINTMENTS =====
    @GetMapping("/appointments")
    public ResponseEntity<?> getAllAppointments() {
        if (!isAdmin()) {
            return forbiddenResponse();
        }

        try {
            List<Map<String, Object>> appointments = appointmentRepository.findAll().stream().map(appointment -> {
                Map<String, Object> appointmentMap = new LinkedHashMap<>();
                appointmentMap.put("id", appointment.getId());
                appointmentMap.put("petName", appointment.getPet() != null ? appointment.getPet().getName() : "Unknown");
                appointmentMap.put("petOwnerName", appointment.getPetOwner() != null ? appointment.getPetOwner().getName() : "Unknown");
                appointmentMap.put("doctorName", appointment.getDoctor() != null ? appointment.getDoctor().getName() : "Unknown");
                appointmentMap.put("appointmentDate", appointment.getAppointmentDate());
                appointmentMap.put("appointmentDateTime", appointment.getAppointmentDateTime());
                appointmentMap.put("status", appointment.getStatus().toString());
                appointmentMap.put("fee", appointment.getFee());
                appointmentMap.put("notes", appointment.getNotes());
                appointmentMap.put("createdAt", appointment.getCreatedAt());
                return appointmentMap;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to fetch appointments"));
        }
    }

    // ===== DOCTOR EARNINGS =====
    @GetMapping("/earnings")
    public ResponseEntity<?> getDoctorEarnings() {
        if (!isAdmin()) {
            return forbiddenResponse();
        }

        try {
            // Get all doctors
            List<User> doctors = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == Role.VETERINARY_DOCTOR)
                    .collect(Collectors.toList());

            // Calculate earnings for each doctor
            List<Map<String, Object>> earnings = doctors.stream().map(doctor -> {
                Map<String, Object> earningMap = new LinkedHashMap<>();
                
                // Get all completed and approved appointments for this doctor
                List<com.petcare.entity.Appointment> appointments = appointmentRepository.findByDoctor(doctor).stream()
                        .filter(apt -> apt.getStatus().toString().equals("APPROVED") || 
                                     apt.getStatus().toString().equals("COMPLETED"))
                        .collect(Collectors.toList());
                
                // Calculate doctor earnings from appointments
                java.math.BigDecimal appointmentEarnings = appointments.stream()
                        .map(apt -> apt.getFee() != null ? apt.getFee() : java.math.BigDecimal.ZERO)
                        .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
                
                earningMap.put("doctorId", doctor.getId());
                earningMap.put("doctorName", doctor.getName());
                earningMap.put("email", doctor.getEmail());
                earningMap.put("phone", doctor.getPhone());
                earningMap.put("totalAppointments", appointments.size());
                earningMap.put("appointmentEarnings", appointmentEarnings.doubleValue());
                earningMap.put("totalEarnings", appointmentEarnings.doubleValue());
                earningMap.put("status", doctor.getStatus().toString());
                
                return earningMap;
            }).sorted((a, b) -> Double.compare((Double) b.get("totalEarnings"), (Double) a.get("totalEarnings")))
             .collect(Collectors.toList());

            return ResponseEntity.ok(earnings);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Failed to fetch earnings: " + e.getMessage()));
        }
    }

    // ===== MARKETPLACE EARNINGS =====
    @GetMapping("/earnings/marketplace")
    public ResponseEntity<?> getMarketplaceEarnings() {
        if (!isAdmin()) {
            return forbiddenResponse();
        }

        try {
            List<com.petcare.entity.Order> orders = orderRepository.findAll().stream()
                    .filter(o -> o.getPaymentStatus().toString().equals("SUCCESS"))
                    .collect(Collectors.toList());
            
            java.math.BigDecimal totalMarketplaceEarnings = orders.stream()
                    .map(o -> o.getTotalAmount() != null ? o.getTotalAmount() : java.math.BigDecimal.ZERO)
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
            
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("totalMarketplaceEarnings", totalMarketplaceEarnings.doubleValue());
            result.put("totalOrders", orders.size());
            result.put("avgOrderValue", orders.size() > 0 ? 
                    totalMarketplaceEarnings.doubleValue() / orders.size() : 0);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Failed to fetch marketplace earnings: " + e.getMessage()));
        }
    }

    // ===== COMPREHENSIVE EARNINGS SUMMARY =====
    @GetMapping("/earnings/summary")
    public ResponseEntity<?> getEarningsSummary() {
        if (!isAdmin()) {
            return forbiddenResponse();
        }

        try {
            // Doctor Earnings
            List<com.petcare.entity.Appointment> allAppointments = appointmentRepository.findAll().stream()
                    .filter(apt -> apt.getStatus().toString().equals("APPROVED") || 
                                 apt.getStatus().toString().equals("COMPLETED"))
                    .collect(Collectors.toList());
            
            java.math.BigDecimal totalDoctorEarnings = allAppointments.stream()
                    .map(apt -> apt.getFee() != null ? apt.getFee() : java.math.BigDecimal.ZERO)
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

            // Marketplace Earnings
            List<com.petcare.entity.Order> orders = orderRepository.findAll().stream()
                    .filter(o -> o.getPaymentStatus().toString().equals("SUCCESS"))
                    .collect(Collectors.toList());
            
            java.math.BigDecimal totalMarketplaceEarnings = orders.stream()
                    .map(o -> o.getTotalAmount() != null ? o.getTotalAmount() : java.math.BigDecimal.ZERO)
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

            // Combined Total
            java.math.BigDecimal totalEarnings = totalDoctorEarnings.add(totalMarketplaceEarnings);

            Map<String, Object> summary = new LinkedHashMap<>();
            summary.put("doctorEarnings", totalDoctorEarnings.doubleValue());
            summary.put("marketplaceEarnings", totalMarketplaceEarnings.doubleValue());
            summary.put("totalEarnings", totalEarnings.doubleValue());
            summary.put("totalAppointments", allAppointments.size());
            summary.put("totalOrders", orders.size());
            summary.put("doctorEarningsPercentage", totalEarnings.doubleValue() > 0 ? 
                    (totalDoctorEarnings.doubleValue() / totalEarnings.doubleValue() * 100) : 0);
            summary.put("marketplaceEarningsPercentage", totalEarnings.doubleValue() > 0 ? 
                    (totalMarketplaceEarnings.doubleValue() / totalEarnings.doubleValue() * 100) : 0);

            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Failed to fetch earnings summary: " + e.getMessage()));
        }
    }
}

