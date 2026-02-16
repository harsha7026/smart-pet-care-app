package com.petcare.controller;

import com.petcare.entity.*;
import com.petcare.model.AppointmentStatus;
import com.petcare.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

@RestController
@RequestMapping("/api/doctor")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class DoctorController {

    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final PetRepository petRepository;
    private final MedicalHistoryRepository medicalHistoryRepository;
    private final HealthMetricRepository healthMetricRepository;
    private final VaccinationRecordRepository vaccinationRecordRepository;

    public DoctorController(UserRepository userRepository,
                           AppointmentRepository appointmentRepository,
                           PetRepository petRepository,
                           MedicalHistoryRepository medicalHistoryRepository,
                           HealthMetricRepository healthMetricRepository,
                           VaccinationRecordRepository vaccinationRecordRepository) {
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
        this.petRepository = petRepository;
        this.medicalHistoryRepository = medicalHistoryRepository;
        this.healthMetricRepository = healthMetricRepository;
        this.vaccinationRecordRepository = vaccinationRecordRepository;
    }

    private Optional<User> getCurrentDoctor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return Optional.empty();
        }
        return userRepository.findByEmail(auth.getName());
    }

    private boolean isDoctor() {
        Optional<User> user = getCurrentDoctor();
        return user.isPresent() && user.get().getRole() == Role.VETERINARY_DOCTOR;
    }

    private ResponseEntity<?> forbiddenResponse() {
        return ResponseEntity.status(403).body(Map.of("message", "Access Denied: Veterinary Doctor role required"));
    }

    private ResponseEntity<?> errorResponse(String message) {
        return ResponseEntity.status(400).body(Map.of("message", message));
    }

    // ===== DOCTOR DASHBOARD =====
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDoctorDashboard() {
        if (!isDoctor()) {
            return forbiddenResponse();
        }

        Optional<User> doctor = getCurrentDoctor();
        if (doctor.isEmpty()) {
            return errorResponse("Doctor not found");
        }

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("name", doctor.get().getName());
        dashboard.put("email", doctor.get().getEmail());
        dashboard.put("phone", doctor.get().getPhone());
        dashboard.put("status", doctor.get().getStatus());
        dashboard.put("approvalStatus", doctor.get().getStatus().toString());

        // Count appointments
        List<Appointment> appointments = appointmentRepository.findAll()
                .stream()
                .filter(a -> a.getDoctor().getId().equals(doctor.get().getId()))
                .collect(Collectors.toList());

        dashboard.put("totalAppointments", appointments.size());
        dashboard.put("pendingAppointments", appointments.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.PENDING)
                .count());
        dashboard.put("completedAppointments", appointments.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.COMPLETED)
                .count());

        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getDoctorStats() {
        if (!isDoctor()) {
            return forbiddenResponse();
        }

        Optional<User> doctorOpt = getCurrentDoctor();
        if (doctorOpt.isEmpty()) {
            return errorResponse("Doctor not found");
        }

        try {
            User doctor = doctorOpt.get();
            Map<String, Object> stats = new HashMap<>();

            // Get basic doctor info
            stats.put("name", doctor.getName());
            stats.put("email", doctor.getEmail());
            stats.put("phone", doctor.getPhone());
            stats.put("specialization", doctor.getSpecialization() != null ? doctor.getSpecialization() : "General Practitioner");
            stats.put("isVerified", doctor.getIsVerified());
            stats.put("verificationStatus", doctor.getIsVerified() ? "VERIFIED" : "PENDING");

            // Initialize appointment counts
            long totalAppointments = 0;
            long pendingAppointments = 0;
            long completedAppointments = 0;
            long rejectedAppointments = 0;

            // Safely get appointments
            try {
                List<Appointment> allAppointments = appointmentRepository.findAll();
                for (Appointment appt : allAppointments) {
                    try {
                        if (appt.getDoctor() != null && appt.getDoctor().getId().equals(doctor.getId())) {
                            totalAppointments++;
                            if (appt.getStatus() == AppointmentStatus.PENDING) pendingAppointments++;
                            else if (appt.getStatus() == AppointmentStatus.COMPLETED) completedAppointments++;
                            else if (appt.getStatus() == AppointmentStatus.REJECTED) rejectedAppointments++;
                        }
                    } catch (Exception e) {
                        // Skip this appointment if there's an error
                        continue;
                    }
                }
            } catch (Exception e) {
                // If we can't fetch appointments, just return 0
                totalAppointments = 0;
            }

            stats.put("totalAppointments", totalAppointments);
            stats.put("pendingAppointments", pendingAppointments);
            stats.put("completedAppointments", completedAppointments);
            stats.put("rejectedAppointments", rejectedAppointments);
            stats.put("totalPatients", 0);
            stats.put("totalPets", 0);

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorStats = new HashMap<>();
            errorStats.put("name", "Doctor");
            errorStats.put("email", "");
            errorStats.put("phone", "");
            errorStats.put("totalAppointments", 0);
            errorStats.put("pendingAppointments", 0);
            errorStats.put("completedAppointments", 0);
            errorStats.put("rejectedAppointments", 0);
            errorStats.put("totalPatients", 0);
            errorStats.put("totalPets", 0);
            errorStats.put("isVerified", false);
            errorStats.put("verificationStatus", "PENDING");
            return ResponseEntity.ok(errorStats);
        }
    }

    // ===== APPOINTMENTS =====
    @GetMapping("/appointments")
    public ResponseEntity<?> getDoctorAppointments() {
        if (!isDoctor()) {
            return forbiddenResponse();
        }

        Optional<User> doctor = getCurrentDoctor();
        if (doctor.isEmpty()) {
            return errorResponse("Doctor not found");
        }

        List<Map<String, Object>> appointments = appointmentRepository.findAll()
                .stream()
                .filter(a -> a.getDoctor().getId().equals(doctor.get().getId()))
                .map(a -> {
                    Map<String, Object> appointment = new HashMap<>();
                    appointment.put("id", a.getId());
                    appointment.put("petId", a.getPet().getId());
                    appointment.put("petName", a.getPet().getName());
                    appointment.put("petSpecies", a.getPet().getSpecies());
                    appointment.put("ownerName", a.getPetOwner().getName());
                    appointment.put("ownerEmail", a.getPetOwner().getEmail());
                    appointment.put("ownerPhone", a.getPetOwner().getPhone());
                    appointment.put("appointmentDate", a.getAppointmentDateTime());
                    appointment.put("reason", a.getReason());
                    appointment.put("status", a.getStatus());
                    appointment.put("notes", a.getNotes());
                    return appointment;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(appointments);
    }

    @PutMapping("/appointments/{appointmentId}/status")
    public ResponseEntity<?> updateAppointmentStatus(@PathVariable Long appointmentId, @RequestBody Map<String, String> body) {
        if (!isDoctor()) {
            return forbiddenResponse();
        }

        Optional<User> doctor = getCurrentDoctor();
        if (doctor.isEmpty()) {
            return errorResponse("Doctor not found");
        }

        Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
        if (appointmentOpt.isEmpty()) {
            return errorResponse("Appointment not found");
        }

        Appointment appointment = appointmentOpt.get();

        // Verify doctor owns this appointment
        if (!appointment.getDoctor().getId().equals(doctor.get().getId())) {
            return forbiddenResponse();
        }

        String newStatus = body.get("status");
        try {
            AppointmentStatus status = AppointmentStatus.valueOf(newStatus);
            appointment.setStatus(status);
            appointmentRepository.save(appointment);
            return ResponseEntity.ok(Map.of("message", "Appointment status updated", "status", status));
        } catch (IllegalArgumentException e) {
            return errorResponse("Invalid appointment status");
        }
    }

    @PutMapping("/appointments/{appointmentId}/notes")
    public ResponseEntity<?> updateAppointmentNotes(@PathVariable Long appointmentId, @RequestBody Map<String, String> body) {
        if (!isDoctor()) {
            return forbiddenResponse();
        }

        Optional<User> doctor = getCurrentDoctor();
        if (doctor.isEmpty()) {
            return errorResponse("Doctor not found");
        }

        Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
        if (appointmentOpt.isEmpty()) {
            return errorResponse("Appointment not found");
        }

        Appointment appointment = appointmentOpt.get();

        // Verify doctor owns this appointment
        if (!appointment.getDoctor().getId().equals(doctor.get().getId())) {
            return forbiddenResponse();
        }

        appointment.setNotes(body.get("notes"));
        appointmentRepository.save(appointment);
        return ResponseEntity.ok(Map.of("message", "Appointment notes updated"));
    }

    // ===== PET HEALTH RECORDS =====
    @GetMapping("/pets")
    public ResponseEntity<?> getDoctorPets() {
        if (!isDoctor()) {
            return forbiddenResponse();
        }

        Optional<User> doctor = getCurrentDoctor();
        if (doctor.isEmpty()) {
            return errorResponse("Doctor not found");
        }

        // Get pets through appointments
        List<Pet> pets = appointmentRepository.findAll()
                .stream()
                .filter(a -> a.getDoctor().getId().equals(doctor.get().getId()))
                .map(Appointment::getPet)
                .distinct()
                .collect(Collectors.toList());

        return ResponseEntity.ok(pets);
    }

    // Medical History
    @PostMapping("/pets/{petId}/medical-history")
    public ResponseEntity<?> addMedicalHistory(@PathVariable Long petId, @RequestBody Map<String, String> body) {
        if (!isDoctor()) {
            return forbiddenResponse();
        }

        Optional<User> doctor = getCurrentDoctor();
        Optional<Pet> pet = petRepository.findById(petId);

        if (doctor.isEmpty() || pet.isEmpty()) {
            return errorResponse("Doctor or Pet not found");
        }

        // Verify doctor has treated this pet
        boolean hasAccess = appointmentRepository.findAll()
                .stream()
                .anyMatch(a -> a.getDoctor().getId().equals(doctor.get().getId()) &&
                              a.getPet().getId().equals(petId));

        if (!hasAccess) {
            return forbiddenResponse();
        }

        MedicalHistory history = new MedicalHistory();
        history.setPet(pet.get());
        history.setVisitDate(LocalDate.parse(body.get("visitDate")));
        history.setVisitType(body.get("visitType"));
        history.setVeterinarianName(body.getOrDefault("veterinarianName", doctor.get().getName()));
        history.setDiagnosis(body.get("diagnosis"));
        history.setTreatment(body.get("treatment"));
        history.setNotes(body.get("notes"));

        medicalHistoryRepository.save(history);
        return ResponseEntity.ok(Map.of("message", "Medical history added", "id", history.getId()));
    }

    @GetMapping("/pets/{petId}/medical-history")
    public ResponseEntity<?> getMedicalHistory(@PathVariable Long petId) {
        if (!isDoctor()) {
            return forbiddenResponse();
        }

        Optional<User> doctor = getCurrentDoctor();
        Optional<Pet> pet = petRepository.findById(petId);

        if (doctor.isEmpty() || pet.isEmpty()) {
            return errorResponse("Doctor or Pet not found");
        }

        // Verify doctor has treated this pet
        boolean hasAccess = appointmentRepository.findAll()
                .stream()
                .anyMatch(a -> a.getDoctor().getId().equals(doctor.get().getId()) &&
                              a.getPet().getId().equals(petId));

        if (!hasAccess) {
            return forbiddenResponse();
        }

        List<MedicalHistory> history = medicalHistoryRepository.findAll()
                .stream()
                .filter(h -> h.getPet().getId().equals(petId))
                .collect(Collectors.toList());

        return ResponseEntity.ok(history);
    }

    // Health Metrics
    @PostMapping("/pets/{petId}/health-metrics")
    public ResponseEntity<?> addHealthMetric(@PathVariable Long petId, @RequestBody Map<String, String> body) {
        if (!isDoctor()) {
            return forbiddenResponse();
        }

        Optional<User> doctor = getCurrentDoctor();
        Optional<Pet> pet = petRepository.findById(petId);

        if (doctor.isEmpty() || pet.isEmpty()) {
            return errorResponse("Doctor or Pet not found");
        }

        // Verify doctor has treated this pet
        boolean hasAccess = appointmentRepository.findAll()
                .stream()
                .anyMatch(a -> a.getDoctor().getId().equals(doctor.get().getId()) &&
                              a.getPet().getId().equals(petId));

        if (!hasAccess) {
            return forbiddenResponse();
        }

        HealthMetric metric = new HealthMetric();
        metric.setPet(pet.get());
        metric.setRecordDate(LocalDate.parse(body.get("recordDate")));
        metric.setWeight(body.containsKey("weight") ? Double.parseDouble(body.get("weight")) : null);
        metric.setCalorieIntake(body.containsKey("calorieIntake") ? Integer.parseInt(body.get("calorieIntake")) : null);
        metric.setActivityLevel(body.containsKey("activityLevel") ? Integer.parseInt(body.get("activityLevel")) : null);
        metric.setStressLevel(body.get("stressLevel"));
        metric.setBloodPressure(body.get("bloodPressure"));
        metric.setNotes(body.get("notes"));
        metric.setVetEntered(true);

        healthMetricRepository.save(metric);
        return ResponseEntity.ok(Map.of("message", "Health metric added", "id", metric.getId()));
    }

    @GetMapping("/pets/{petId}/health-metrics")
    public ResponseEntity<?> getHealthMetrics(@PathVariable Long petId) {
        if (!isDoctor()) {
            return forbiddenResponse();
        }

        Optional<User> doctor = getCurrentDoctor();
        Optional<Pet> pet = petRepository.findById(petId);

        if (doctor.isEmpty() || pet.isEmpty()) {
            return errorResponse("Doctor or Pet not found");
        }

        // Verify doctor has treated this pet
        boolean hasAccess = appointmentRepository.findAll()
                .stream()
                .anyMatch(a -> a.getDoctor().getId().equals(doctor.get().getId()) &&
                              a.getPet().getId().equals(petId));

        if (!hasAccess) {
            return forbiddenResponse();
        }

        List<HealthMetric> metrics = healthMetricRepository.findAll()
                .stream()
                .filter(m -> m.getPet().getId().equals(petId))
                .collect(Collectors.toList());

        return ResponseEntity.ok(metrics);
    }

    // Vaccination Records
    @PostMapping("/pets/{petId}/vaccination-records")
    public ResponseEntity<?> addVaccinationRecord(@PathVariable Long petId, @RequestBody Map<String, String> body) {
        if (!isDoctor()) {
            return forbiddenResponse();
        }

        Optional<User> doctor = getCurrentDoctor();
        Optional<Pet> pet = petRepository.findById(petId);

        if (doctor.isEmpty() || pet.isEmpty()) {
            return errorResponse("Doctor or Pet not found");
        }

        // Verify doctor has treated this pet
        boolean hasAccess = appointmentRepository.findAll()
                .stream()
                .anyMatch(a -> a.getDoctor().getId().equals(doctor.get().getId()) &&
                              a.getPet().getId().equals(petId));

        if (!hasAccess) {
            return forbiddenResponse();
        }

        VaccinationRecord record = new VaccinationRecord();
        record.setPet(pet.get());
        record.setVaccineName(body.get("vaccineName"));
        record.setAdministeredDate(LocalDate.parse(body.get("administeredDate")));
        record.setNextDueDate(LocalDate.parse(body.get("nextDueDate")));
        record.setStatus(body.getOrDefault("status", "COMPLETED"));
        record.setNotes(body.get("notes"));

        vaccinationRecordRepository.save(record);
        return ResponseEntity.ok(Map.of("message", "Vaccination record added", "id", record.getId()));
    }

    @GetMapping("/pets/{petId}/vaccination-records")
    public ResponseEntity<?> getVaccinationRecords(@PathVariable Long petId) {
        if (!isDoctor()) {
            return forbiddenResponse();
        }

        Optional<User> doctor = getCurrentDoctor();
        Optional<Pet> pet = petRepository.findById(petId);

        if (doctor.isEmpty() || pet.isEmpty()) {
            return errorResponse("Doctor or Pet not found");
        }

        // Verify doctor has treated this pet
        boolean hasAccess = appointmentRepository.findAll()
                .stream()
                .anyMatch(a -> a.getDoctor().getId().equals(doctor.get().getId()) &&
                              a.getPet().getId().equals(petId));

        if (!hasAccess) {
            return forbiddenResponse();
        }

        List<VaccinationRecord> records = vaccinationRecordRepository.findAll()
                .stream()
                .filter(r -> r.getPet().getId().equals(petId))
                .collect(Collectors.toList());

        return ResponseEntity.ok(records);
    }

    // ===== DOCTOR PROFILE =====
    @GetMapping("/profile")
    public ResponseEntity<?> getDoctorProfile() {
        if (!isDoctor()) {
            return forbiddenResponse();
        }

        Optional<User> doctor = getCurrentDoctor();
        if (doctor.isEmpty()) {
            return errorResponse("Doctor not found");
        }

        return ResponseEntity.ok(doctor.get());
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateDoctorProfile(@RequestBody Map<String, String> body) {
        if (!isDoctor()) {
            return forbiddenResponse();
        }

        Optional<User> doctorOpt = getCurrentDoctor();
        if (doctorOpt.isEmpty()) {
            return errorResponse("Doctor not found");
        }

        User doctor = doctorOpt.get();

        // Update allowed fields
        if (body.containsKey("name") && !body.get("name").isBlank()) {
            doctor.setName(body.get("name"));
        }
        if (body.containsKey("phone") && !body.get("phone").isBlank()) {
            doctor.setPhone(body.get("phone"));
        }

        userRepository.save(doctor);
        return ResponseEntity.ok(Map.of("message", "Profile updated"));
    }

    @PostMapping("/upload-verification-document")
    public ResponseEntity<?> uploadVerificationDocument(@RequestParam("document") String documentBase64) {
        if (!isDoctor()) {
            return forbiddenResponse();
        }

        if (documentBase64 == null || documentBase64.isBlank()) {
            return errorResponse("Document URL is required");
        }

        Optional<User> doctorOpt = getCurrentDoctor();
        if (doctorOpt.isEmpty()) {
            return errorResponse("Doctor not found");
        }

        User doctor = doctorOpt.get();
        doctor.setDocumentUrl(documentBase64);
        doctor.setIsVerified(false); // Reset verification when new document uploaded
        userRepository.save(doctor);

        return ResponseEntity.ok(Map.of(
                "message", "Document uploaded successfully. Awaiting admin verification.",
                "documentUrl", documentBase64,
                "isVerified", false
        ));
    }

    @PostMapping("/submit-verification-documents")
    public ResponseEntity<?> submitVerificationDocuments(@RequestBody Map<String, Object> payload) {
        if (!isDoctor()) {
            return forbiddenResponse();
        }

        Optional<User> doctorOpt = getCurrentDoctor();
        if (doctorOpt.isEmpty()) {
            return errorResponse("Doctor not found");
        }

        @SuppressWarnings("unchecked")
        Map<String, Map<String, String>> documents = (Map<String, Map<String, String>>) payload.get("documents");
        
        if (documents == null || documents.isEmpty()) {
            return errorResponse("At least degree and license are required");
        }

        // Check if mandatory documents are present
        if (!documents.containsKey("degree") || !documents.containsKey("license")) {
            return errorResponse("Medical Degree and License are mandatory");
        }

        try {
            User doctor = doctorOpt.get();
            
            // Store complete document objects (with name and base64 data) as JSON
            ObjectMapper mapper = new ObjectMapper();
            String documentsJson = mapper.writeValueAsString(documents);
            
            doctor.setDocumentUrl(documentsJson); // Store full document objects
            doctor.setIsVerified(false); // Mark as pending verification
            userRepository.save(doctor);

            List<String> submittedDocs = new ArrayList<>(documents.keySet());

            return ResponseEntity.ok(Map.of(
                    "message", "All documents submitted successfully! Awaiting admin verification.",
                    "documents", submittedDocs,
                    "isVerified", false,
                    "count", submittedDocs.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to submit documents: " + e.getMessage()));
        }
    }

    @GetMapping("/verification-status")
    public ResponseEntity<?> getVerificationStatus() {
        if (!isDoctor()) {
            return forbiddenResponse();
        }

        Optional<User> doctorOpt = getCurrentDoctor();
        if (doctorOpt.isEmpty()) {
            return errorResponse("Doctor not found");
        }

        User doctor = doctorOpt.get();
        List<String> documents = new ArrayList<>();
        
        if (doctor.getDocumentUrl() != null && !doctor.getDocumentUrl().isBlank()) {
            try {
                // Try parsing as JSON (new format with full document objects)
                ObjectMapper mapper = new ObjectMapper();
                @SuppressWarnings("unchecked")
                Map<String, Map<String, String>> docsMap = mapper.readValue(doctor.getDocumentUrl(), Map.class);
                documents = new ArrayList<>(docsMap.keySet());
            } catch (Exception e) {
                // Fall back to pipe-separated format (old format)
                String[] docNames = doctor.getDocumentUrl().split("\\|");
                documents = Arrays.asList(docNames);
            }
        }

        return ResponseEntity.ok(Map.of(
                "isVerified", doctor.getIsVerified(),
                "documents", documents,
                "message", doctor.getIsVerified() ? "Your profile is verified" : "Your profile is pending verification"
        ));
    }

    @GetMapping("/{doctorId}/documents")
    public ResponseEntity<?> getDoctorDocuments(@PathVariable Long doctorId) {
        // Allow admin and the doctor themselves to view documents
        Optional<User> doctor = userRepository.findById(doctorId);
        if (doctor.isEmpty()) {
            return errorResponse("Doctor not found");
        }

        User doctorUser = doctor.get();
        if (doctorUser.getDocumentUrl() == null || doctorUser.getDocumentUrl().isBlank()) {
            return ResponseEntity.ok(Map.of(
                    "documents", new HashMap<>(),
                    "message", "No documents uploaded"
            ));
        }

        try {
            // Try to parse JSON format with full document objects
            ObjectMapper mapper = new ObjectMapper();
            @SuppressWarnings("unchecked")
            Map<String, Map<String, String>> documentsMap = mapper.readValue(doctorUser.getDocumentUrl(), Map.class);
            
            return ResponseEntity.ok(Map.of(
                    "documents", documentsMap,
                    "message", "Documents retrieved successfully"
            ));
        } catch (Exception e) {
            // Fall back to old format (pipe-delimited or legacy format)
            try {
                String docString = doctorUser.getDocumentUrl();
                Map<String, Map<String, String>> documentsMap = new HashMap<>();
                
                // If it's pipe-delimited (old format with just names)
                if (docString.contains("|")) {
                    String[] docNames = docString.split("\\|");
                    for (String docName : docNames) {
                        documentsMap.put(docName, Map.of(
                                "name", docName,
                                "type", "unknown",
                                "data", ""
                        ));
                    }
                } else {
                    // Single document
                    documentsMap.put("document", Map.of(
                            "name", docString,
                            "type", "unknown",
                            "data", ""
                    ));
                }
                
                return ResponseEntity.ok(Map.of(
                        "documents", documentsMap,
                        "message", "Documents retrieved (legacy format)"
                ));
            } catch (Exception ex) {
                return ResponseEntity.ok(Map.of(
                        "documents", new HashMap<>(),
                        "message", "Unable to parse documents: " + ex.getMessage()
                ));
            }
        }
    }
}