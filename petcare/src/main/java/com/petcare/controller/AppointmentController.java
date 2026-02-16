package com.petcare.controller;

import com.petcare.dto.AppointmentResponse;
import com.petcare.dto.BookAppointmentRequest;
import com.petcare.dto.DoctorInfo;
import com.petcare.dto.PaymentVerificationRequest;
import com.petcare.dto.PetHealthSummary;
import com.petcare.entity.Appointment;
import com.petcare.service.AppointmentService;
import com.petcare.repository.UserRepository;
import com.petcare.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private UserRepository userRepository;

    // Get all doctors for booking form
    @GetMapping("/doctors")
    public ResponseEntity<List<DoctorInfo>> getAllDoctors() {
        try {
            List<DoctorInfo> doctors = appointmentService.getAllDoctors();
            return ResponseEntity.ok(doctors);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Get doctor by ID
    @GetMapping("/doctors/{doctorId}")
    public ResponseEntity<DoctorInfo> getDoctorById(@PathVariable Long doctorId) {
        try {
            DoctorInfo doctor = appointmentService.getDoctorById(doctorId);
            if (doctor != null) {
                return ResponseEntity.ok(doctor);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Create appointment (returns pending appointment with order ID for payment)
    @PostMapping
    public ResponseEntity<Map<String, Object>> createAppointment(
            @RequestBody BookAppointmentRequest request,
            HttpSession session) {
        try {
            Long petOwnerId = (Long) session.getAttribute("userId");
            if (petOwnerId == null) {
                // Fallback to authenticated principal if session attribute missing
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated()) {
                    String email = auth.getName();
                    var userOpt = userRepository.findByEmail(email);
                    if (userOpt.isPresent()) {
                        petOwnerId = userOpt.get().getId();
                    }
                }
                if (petOwnerId == null) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(Map.of("error", "User not authenticated"));
                }
            }

            Appointment appointment = appointmentService.createAppointmentPending(
                    petOwnerId, request.getDoctorId(), request);

            Map<String, Object> response = new HashMap<>();
            response.put("appointmentId", appointment.getId());
            response.put("fee", appointment.getFee());
            response.put("message", "Appointment created. Proceed to payment.");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Update appointment with Razorpay order ID
    @PutMapping("/{appointmentId}/order/{orderId}")
    public ResponseEntity<Map<String, String>> updateOrderId(
            @PathVariable Long appointmentId,
            @PathVariable String orderId) {
        try {
            appointmentService.updateRazorpayOrderId(appointmentId, orderId);
            return ResponseEntity.ok(Map.of("message", "Order ID updated"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Create Razorpay order for an appointment
    @PostMapping("/{appointmentId}/create-order")
    public ResponseEntity<Map<String, Object>> createOrder(@PathVariable Long appointmentId) {
        try {
            Map<String, Object> order = appointmentService.createPaymentOrder(appointmentId);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Verify payment and confirm appointment
    @PostMapping("/verify-payment")
    public ResponseEntity<Map<String, Object>> verifyPayment(
            @RequestBody PaymentVerificationRequest request) {
        try {
            Appointment appointment = appointmentService.verifyPayment(
                    request.getAppointmentId(),
                    request.getRazorpayPaymentId(),
                    request.getRazorpayOrderId(),
                    request.getRazorpaySignature());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Payment verified successfully");
            response.put("appointmentId", appointment.getId());
            response.put("status", appointment.getStatus());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Get appointments for pet owner
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AppointmentResponse>> getUserAppointments(
            @PathVariable Long userId,
            HttpServletRequest request) {
        try {
            // Get userId from multiple sources
            Long authenticatedUserId = null;
            
            // Try request attribute first (set by JWT filter)
            Object attr = request.getAttribute("userId");
            if (attr != null) {
                authenticatedUserId = (Long) attr;
            } else {
                // Try extracting from SecurityContext
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated() && auth.getName() != null) {
                    var userOpt = userRepository.findByEmail(auth.getName());
                    if (userOpt.isPresent()) {
                        authenticatedUserId = userOpt.get().getId();
                    }
                }
            }
            
            System.out.println("Authenticated userId: " + authenticatedUserId + ", Path userId: " + userId);

            List<AppointmentResponse> appointments = appointmentService.getAppointmentsForPetOwner(userId);
            System.out.println("Found " + appointments.size() + " appointments for user " + userId);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            System.out.println("Error fetching appointments: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
    }

    // Get appointments for doctor
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentResponse>> getDoctorAppointments(
            @PathVariable Long doctorId,
            HttpServletRequest request) {
        try {
            // Get userId from multiple sources
            Long authenticatedUserId = null;
            
            // Try request attribute first (set by JWT filter)
            Object attr = request.getAttribute("userId");
            if (attr != null) {
                authenticatedUserId = (Long) attr;
            } else {
                // Try extracting from SecurityContext
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated() && auth.getName() != null) {
                    var userOpt = userRepository.findByEmail(auth.getName());
                    if (userOpt.isPresent()) {
                        authenticatedUserId = userOpt.get().getId();
                    }
                }
            }
            
            System.out.println("Authenticated doctorId: " + authenticatedUserId + ", Path doctorId: " + doctorId);

            List<AppointmentResponse> appointments = appointmentService.getAppointmentsForDoctor(doctorId);
            System.out.println("Found " + appointments.size() + " appointments for doctor " + doctorId);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            System.out.println("Error fetching doctor appointments: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
    }

    // Get single appointment
    @GetMapping("/{appointmentId}")
    public ResponseEntity<AppointmentResponse> getAppointment(@PathVariable Long appointmentId) {
        try {
            AppointmentResponse appointment = appointmentService.getAppointmentById(appointmentId);
            if (appointment != null) {
                return ResponseEntity.ok(appointment);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Get pet health summary for appointment review (doctor use)
    @GetMapping("/{appointmentId}/pet-health")
    public ResponseEntity<PetHealthSummary> getPetHealthForAppointment(@PathVariable Long appointmentId, HttpSession session) {
        try {
            Long doctorId = (Long) session.getAttribute("userId");
            if (doctorId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            
            PetHealthSummary healthSummary = appointmentService.getPetHealthSummaryForAppointment(appointmentId, doctorId);
            return ResponseEntity.ok(healthSummary);
        } catch (Exception e) {
            System.out.println("Error fetching pet health: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // Approve appointment (doctor action) with time assignment
    @PutMapping("/{appointmentId}/approve")
    public ResponseEntity<Map<String, Object>> approveAppointment(
            @PathVariable Long appointmentId,
               @RequestBody(required = false) Map<String, String> body,
               HttpServletRequest request) {
        try {
                // Debug: Log all headers
                System.out.println("=== APPROVE ENDPOINT REQUEST DEBUG ===");
                System.out.println("Path: " + request.getRequestURI());
                System.out.println("Authorization header: " + request.getHeader("Authorization"));
                System.out.println("Content-Type: " + request.getHeader("Content-Type"));
            
            // Get doctor from JWT token
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                System.out.println("Authentication object: " + auth);
                if (auth != null) {
                    System.out.println("  isAuthenticated: " + auth.isAuthenticated());
                    System.out.println("  Name: " + auth.getName());
                    System.out.println("  Authorities: " + auth.getAuthorities());
                }
            
            if (auth == null || !auth.isAuthenticated()) {
                    System.out.println("AUTHORIZATION FAILED - returning 401");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not authenticated"));
            }

            User doctor = userRepository.findByEmail(auth.getName()).orElse(null);
            if (doctor == null) {
                    System.out.println("Doctor not found for email: " + auth.getName());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Doctor not found"));
            }

            Long doctorId = doctor.getId();
            System.out.println("Approve endpoint - Doctor ID: " + doctorId + ", Email: " + auth.getName());

            // Parse time if provided
            LocalTime appointmentTime = null;
            if (body != null && body.containsKey("appointmentTime")) {
                appointmentTime = LocalTime.parse(body.get("appointmentTime"));
            }

            Appointment appointment = appointmentService.approveAppointment(appointmentId, doctorId, appointmentTime);
            System.out.println("Appointment approved successfully. New status: " + appointment.getStatus());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Appointment approved");
            response.put("status", appointment.getStatus());
            if (appointment.getAppointmentTime() != null) {
                response.put("appointmentTime", appointment.getAppointmentTime().toString());
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("Error in approve endpoint: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Reject appointment (doctor action)
    @PutMapping("/{appointmentId}/reject")
    public ResponseEntity<Map<String, Object>> rejectAppointment(
            @PathVariable Long appointmentId) {
        try {
            // Get doctor from JWT token
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not authenticated"));
            }

            User doctor = userRepository.findByEmail(auth.getName()).orElse(null);
            if (doctor == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Doctor not found"));
            }

            Long doctorId = doctor.getId();
            Appointment appointment = appointmentService.rejectAppointment(appointmentId, doctorId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Appointment rejected");
            response.put("status", appointment.getStatus());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Complete appointment (doctor action)
    @PutMapping("/{appointmentId}/complete")
    public ResponseEntity<Map<String, Object>> completeAppointment(
            @PathVariable Long appointmentId) {
        try {
            // Get doctor from JWT token
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not authenticated"));
            }

            User doctor = userRepository.findByEmail(auth.getName()).orElse(null);
            if (doctor == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Doctor not found"));
            }

            Long doctorId = doctor.getId();
            Appointment appointment = appointmentService.completeAppointment(appointmentId, doctorId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Appointment marked as completed");
            response.put("status", appointment.getStatus());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Add or update appointment notes (doctor action)
    @PutMapping("/{appointmentId}/notes")
    public ResponseEntity<Map<String, Object>> updateAppointmentNotes(
            @PathVariable Long appointmentId,
            @RequestBody Map<String, String> body,
            HttpServletRequest request) {
        try {
            // Get doctor from JWT token
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not authenticated"));
            }

            User doctor = userRepository.findByEmail(auth.getName()).orElse(null);
            if (doctor == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Doctor not found"));
            }

            Long doctorId = doctor.getId();
            if (doctorId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not authenticated"));
            }

            // Use service layer to update notes
            appointmentService.updateAppointmentNotes(doctorId, appointmentId, body.get("notes"));
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Notes updated successfully");
            response.put("notes", body.get("notes"));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
