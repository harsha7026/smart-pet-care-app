package com.petcare.service;

import com.petcare.dto.AppointmentResponse;
import com.petcare.dto.BookAppointmentRequest;
import com.petcare.dto.DoctorInfo;
import com.petcare.dto.PetHealthSummary;
import com.petcare.dto.AppointmentEmailDTO;
import com.petcare.entity.Appointment;
import com.petcare.entity.Pet;
import com.petcare.entity.User;
import com.petcare.entity.Role;
import com.petcare.entity.MedicalHistory;
import com.petcare.entity.VaccinationRecord;
import com.petcare.entity.HealthMetric;
import com.petcare.entity.HealthReminder;
import com.petcare.entity.NotificationType;
import com.petcare.model.AppointmentStatus;
import com.petcare.model.PaymentStatus;
import com.petcare.repository.AppointmentRepository;
import com.petcare.repository.UserRepository;
import com.petcare.repository.PetRepository;
import com.petcare.repository.MedicalHistoryRepository;
import com.petcare.repository.VaccinationRecordRepository;
import com.petcare.repository.HealthMetricRepository;
import com.petcare.repository.HealthReminderRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class AppointmentService {

    private static final Logger log = LoggerFactory.getLogger(AppointmentService.class);

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private MedicalHistoryRepository medicalHistoryRepository;

    @Autowired
    private VaccinationRecordRepository vaccinationRecordRepository;

    @Autowired
    private HealthMetricRepository healthMetricRepository;

    @Autowired
    private HealthReminderRepository healthReminderRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EmailService emailService;

    @Value("${razorpay.key-id}")
    private String razorpayKeyId;

    @Value("${razorpay.key-secret}")
    private String razorpayKeySecret;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    // Get all doctors (for booking form dropdown)
    public List<DoctorInfo> getAllDoctors() {
        List<User> doctors = userRepository.findByRole(Role.VETERINARY_DOCTOR);
        return doctors.stream()
                .map(doctor -> new DoctorInfo(
                        doctor.getId(),
                        doctor.getName(),
                        doctor.getEmail(),
                        doctor.getPhone(),
                        doctor.getSpecialization(),
                        doctor.getConsultationFee() != null ? doctor.getConsultationFee() : BigDecimal.valueOf(500)
                ))
                .collect(Collectors.toList());
    }

    // Get doctor by ID
    public DoctorInfo getDoctorById(Long doctorId) {
        Optional<User> doctorOpt = userRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            User doctor = doctorOpt.get();
            return new DoctorInfo(
                    doctor.getId(),
                    doctor.getName(),
                    doctor.getEmail(),
                    doctor.getPhone(),
                    doctor.getSpecialization(),
                    doctor.getConsultationFee() != null ? doctor.getConsultationFee() : BigDecimal.valueOf(500)
            );
        }
        return null;
    }

    // Create appointment (before payment)
    public Appointment createAppointmentPending(Long petOwnerId, Long doctorId, BookAppointmentRequest request) {
        Optional<User> petOwnerOpt = userRepository.findById(petOwnerId);
        Optional<User> doctorOpt = userRepository.findById(doctorId);

        if (!petOwnerOpt.isPresent() || !doctorOpt.isPresent()) {
            throw new RuntimeException("Pet owner or doctor not found");
        }

        User petOwner = petOwnerOpt.get();
        User doctor = doctorOpt.get();

        // Get the selected pet
        Optional<Pet> petOpt = petRepository.findById(request.getPetId());
        if (!petOpt.isPresent()) {
            throw new RuntimeException("Pet not found");
        }
        Pet pet = petOpt.get();
        
        // Verify pet belongs to the owner
        if (!pet.getUser().getId().equals(petOwnerId)) {
            throw new RuntimeException("Pet does not belong to this owner");
        }

        Appointment appointment = new Appointment();
        appointment.setPetOwner(petOwner);
        appointment.setDoctor(doctor);
        appointment.setPet(pet);
        appointment.setAppointmentDate(request.getAppointmentDate());
        // Time will be assigned by doctor later
        if (request.getAppointmentTime() != null) {
            appointment.setAppointmentTime(request.getAppointmentTime());
            appointment.setAppointmentDateTime(LocalDateTime.of(request.getAppointmentDate(), request.getAppointmentTime()));
        }
        appointment.setReason(request.getReason());
        appointment.setFee(request.getFee());
        appointment.setStatus(AppointmentStatus.PENDING);
        appointment.setPaymentStatus(PaymentStatus.PENDING);

        return appointmentRepository.save(appointment);
    }

    // Update appointment with Razorpay order ID (fallback/manual use)
    public void updateRazorpayOrderId(Long appointmentId, String orderId) {
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
        if (appointmentOpt.isPresent()) {
            Appointment appointment = appointmentOpt.get();
            appointment.setRazorpayOrderId(orderId);
            appointmentRepository.save(appointment);
        }
    }

    // Create a Razorpay order for this appointment and persist the order id
    public Map<String, Object> createPaymentOrder(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        BigDecimal fee = appointment.getFee() != null ? appointment.getFee() : BigDecimal.ZERO;
        long amountInPaise = fee.movePointRight(2).setScale(0, RoundingMode.HALF_UP).longValueExact();
        if (amountInPaise <= 0) {
            throw new RuntimeException("Consultation fee must be greater than 0");
        }

        try {
            RazorpayClient client = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

            JSONObject notes = new JSONObject();
            notes.put("appointmentId", appointmentId);
            notes.put("doctorId", appointment.getDoctor().getId());

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "appt_" + appointmentId);
            orderRequest.put("payment_capture", 1);
            orderRequest.put("notes", notes);

            Order order = client.orders.create(orderRequest);
            appointment.setRazorpayOrderId(order.get("id"));
            appointmentRepository.save(appointment);

            Map<String, Object> payload = new HashMap<>();
            payload.put("orderId", order.get("id"));
            payload.put("amount", order.get("amount"));
            payload.put("currency", order.get("currency"));
            payload.put("keyId", razorpayKeyId);
            payload.put("appointmentId", appointmentId);
            payload.put("doctorName", appointment.getDoctor().getName());
            payload.put("petOwnerName", appointment.getPetOwner().getName());
            return payload;
        } catch (RazorpayException ex) {
            throw new RuntimeException("Failed to create Razorpay order: " + ex.getMessage(), ex);
        }
    }

    // Verify payment signature and confirm payment
    public Appointment verifyPayment(Long appointmentId, String razorpayPaymentId, String razorpayOrderId, String razorpaySignature) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (razorpayOrderId == null || razorpayPaymentId == null || razorpaySignature == null) {
            throw new RuntimeException("Missing payment details");
        }

        JSONObject attributes = new JSONObject();
        attributes.put("razorpay_order_id", razorpayOrderId);
        attributes.put("razorpay_payment_id", razorpayPaymentId);
        attributes.put("razorpay_signature", razorpaySignature);

        try {
            boolean isValid = Utils.verifyPaymentSignature(attributes, razorpayKeySecret);
            if (!isValid) {
                throw new RuntimeException("Invalid payment signature");
            }
        } catch (RazorpayException ex) {
            throw new RuntimeException("Payment verification failed: " + ex.getMessage(), ex);
        }

        appointment.setRazorpayOrderId(razorpayOrderId);
        appointment.setRazorpayPaymentId(razorpayPaymentId);
        appointment.setPaymentStatus(PaymentStatus.SUCCESS);
        appointment.setStatus(AppointmentStatus.PENDING);

        Appointment savedAppointment = appointmentRepository.save(appointment);

        // Notify doctor about new appointment
        notificationService.createNotification(
                appointment.getDoctor().getId(),
                NotificationType.APPOINTMENT_BOOKED,
                "New Appointment Booked",
                "Pet owner " + appointment.getPetOwner().getName() + " booked an appointment for " + appointment.getPet().getName(),
                savedAppointment.getId(),
                "APPOINTMENT"
        );

        // Send appointment booked emails (pet owner and doctor)
        try {
            sendAppointmentBookedEmails(savedAppointment);
        } catch (Exception e) {
            log.error("Failed to send appointment booked emails: {}", e.getMessage());
        }

        return savedAppointment;
    }

    // Get appointments for pet owner
    public List<AppointmentResponse> getAppointmentsForPetOwner(Long petOwnerId) {
        List<Appointment> appointments = appointmentRepository.findByPetOwnerIdOrderByAppointmentDateDesc(petOwnerId);
        return appointments.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // Get appointments for doctor
    public List<AppointmentResponse> getAppointmentsForDoctor(Long doctorId) {
        List<Appointment> appointments = appointmentRepository.findByDoctorIdOrderByAppointmentDateDesc(doctorId);
        return appointments.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // Approve appointment (doctor action) with time assignment
    public Appointment approveAppointment(Long appointmentId, Long doctorId, LocalTime appointmentTime) {
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
        if (!appointmentOpt.isPresent()) {
            throw new RuntimeException("Appointment not found");
        }

        Appointment appointment = appointmentOpt.get();
        
        // Verify doctor owns this appointment
        if (!appointment.getDoctor().getId().equals(doctorId)) {
            throw new RuntimeException("Unauthorized access");
        }

        // Allow both SUCCESS and PENDING payment status for development
        if (appointment.getPaymentStatus() != PaymentStatus.SUCCESS && appointment.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new RuntimeException("Payment not confirmed");
        }

        // Assign time if provided
        if (appointmentTime != null) {
            appointment.setAppointmentTime(appointmentTime);
            appointment.setAppointmentDateTime(LocalDateTime.of(appointment.getAppointmentDate(), appointmentTime));
        }

        appointment.setStatus(AppointmentStatus.APPROVED);
        Appointment savedAppointment = appointmentRepository.save(appointment);

        // Notify pet owner about approval
        notificationService.createNotification(
                appointment.getPetOwner().getId(),
                NotificationType.APPOINTMENT_APPROVED,
                "Appointment Approved",
                "Your appointment with Dr. " + appointment.getDoctor().getName() + " has been approved" +
                        (appointmentTime != null ? " for " + appointmentTime : ""),
                savedAppointment.getId(),
                "APPOINTMENT"
        );

        // Send appointment status update email
        try {
            sendAppointmentStatusUpdateEmail(savedAppointment, true, false, false, null);
        } catch (Exception e) {
            log.error("Failed to send appointment approval email: {}", e.getMessage());
        }

        return savedAppointment;
    }

    // Reject appointment (doctor action)
    public Appointment rejectAppointment(Long appointmentId, Long doctorId) {
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
        if (!appointmentOpt.isPresent()) {
            throw new RuntimeException("Appointment not found");
        }

        Appointment appointment = appointmentOpt.get();
        
        // Verify doctor owns this appointment
        if (!appointment.getDoctor().getId().equals(doctorId)) {
            throw new RuntimeException("Unauthorized access");
        }

        appointment.setStatus(AppointmentStatus.REJECTED);
        Appointment savedAppointment = appointmentRepository.save(appointment);

        // Notify pet owner about rejection
        notificationService.createNotification(
                appointment.getPetOwner().getId(),
                NotificationType.APPOINTMENT_REJECTED,
                "Appointment Rejected",
                "Your appointment with Dr. " + appointment.getDoctor().getName() + " has been rejected",
                savedAppointment.getId(),
                "APPOINTMENT"
        );

        // Send appointment status update email
        try {
            sendAppointmentStatusUpdateEmail(savedAppointment, false, true, false, null);
        } catch (Exception e) {
            log.error("Failed to send appointment rejection email: {}", e.getMessage());
        }

        return savedAppointment;
    }

    // Complete appointment (doctor action)
    public Appointment completeAppointment(Long appointmentId, Long doctorId) {
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
        if (!appointmentOpt.isPresent()) {
            throw new RuntimeException("Appointment not found");
        }

        Appointment appointment = appointmentOpt.get();
        
        // Verify doctor owns this appointment
        if (!appointment.getDoctor().getId().equals(doctorId)) {
            throw new RuntimeException("Unauthorized access");
        }

        if (appointment.getStatus() != AppointmentStatus.APPROVED) {
            throw new RuntimeException("Only approved appointments can be marked complete");
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        Appointment savedAppointment = appointmentRepository.save(appointment);

        // Notify pet owner about completion
        notificationService.createNotification(
                appointment.getPetOwner().getId(),
                NotificationType.APPOINTMENT_COMPLETED,
                "Appointment Completed",
                "Your appointment with Dr. " + appointment.getDoctor().getName() + " has been completed",
                savedAppointment.getId(),
                "APPOINTMENT"
        );

        return savedAppointment;
    }

    // Get appointment by ID
    public AppointmentResponse getAppointmentById(Long appointmentId) {
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
        if (appointmentOpt.isPresent()) {
            return mapToResponse(appointmentOpt.get());
        }
        return null;
    }

    // Helper method to map Appointment to AppointmentResponse
    private AppointmentResponse mapToResponse(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getDoctor().getId(),
                appointment.getDoctor().getName(),
                appointment.getPetOwner().getId(),
                appointment.getPetOwner().getName(),
                appointment.getPet().getId(),
                appointment.getPet().getName(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                appointment.getReason(),
                appointment.getFee(),
                appointment.getStatus(),
                appointment.getPaymentStatus(),
                appointment.getRazorpayOrderId(),
                appointment.getRazorpayPaymentId(),
                appointment.getCreatedAt(),
                appointment.getUpdatedAt()
        );
    }

    // Cancel appointment if payment failed
    public void cancelAppointmentByOrderId(String orderId) {
        Optional<Appointment> appointmentOpt = appointmentRepository.findByRazorpayOrderId(orderId);
        if (appointmentOpt.isPresent()) {
            Appointment appointment = appointmentOpt.get();
            appointment.setPaymentStatus(PaymentStatus.FAILED);
            appointmentRepository.delete(appointment);
        }
    }

    // Update appointment notes (doctor action)
    public void updateAppointmentNotes(Long doctorId, Long appointmentId, String notes) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // Verify doctor owns this appointment
        if (!appointment.getDoctor().getId().equals(doctorId)) {
            throw new RuntimeException("Unauthorized access");
        }

        appointment.setNotes(notes);
        appointmentRepository.save(appointment);
    }

    // Get pet health summary for appointment (doctor views before approving)
    public PetHealthSummary getPetHealthSummaryForAppointment(Long appointmentId, Long doctorId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // Verify doctor owns this appointment
        if (!appointment.getDoctor().getId().equals(doctorId)) {
            throw new RuntimeException("Unauthorized access");
        }

        Pet pet = appointment.getPet();
        PetHealthSummary summary = new PetHealthSummary();
        summary.petId = pet.getId();
        summary.petName = pet.getName();
        summary.species = pet.getSpecies();
        summary.breed = pet.getBreed();
        summary.age = pet.getAge();
        summary.weight = pet.getWeight() != null ? BigDecimal.valueOf(pet.getWeight()) : null;
        summary.medicalNotes = pet.getMedicalNotes();

        // Get medical history
        List<MedicalHistory> medHistory = medicalHistoryRepository.findByPetOrderByVisitDateDesc(pet);
        summary.medicalHistory = medHistory.stream().map(mh -> {
            PetHealthSummary.MedicalHistoryItem item = new PetHealthSummary.MedicalHistoryItem();
            item.id = mh.getId();
            item.visitDate = mh.getVisitDate();
            item.diagnosis = mh.getDiagnosis();
            item.treatment = mh.getTreatment();
            item.prescription = mh.getPrescription();
            item.notes = mh.getNotes();
            return item;
        }).collect(Collectors.toList());

        // Get vaccination records
        List<VaccinationRecord> vaccinations = vaccinationRecordRepository.findByPetOrderByDueDateAsc(pet);
        summary.vaccinations = vaccinations.stream().map(v -> {
            PetHealthSummary.VaccinationItem item = new PetHealthSummary.VaccinationItem();
            item.id = v.getId();
            item.vaccineName = v.getVaccineName();
            item.administeredDate = v.getAdministeredDate();
            item.dueDate = v.getDueDate();
            item.status = v.getStatus();
            item.notes = v.getNotes();
            return item;
        }).collect(Collectors.toList());

        // Get health metrics (recent 10)
        List<HealthMetric> metrics = healthMetricRepository.findByPetOrderByRecordDateDesc(pet).stream()
                .limit(10)
                .collect(Collectors.toList());
        summary.healthMetrics = metrics.stream().map(hm -> {
            PetHealthSummary.HealthMetricItem item = new PetHealthSummary.HealthMetricItem();
            item.id = hm.getId();
            item.recordDate = hm.getRecordDate();
            item.weight = hm.getWeight();
            item.calorieIntake = hm.getCalorieIntake();
            item.activityLevel = hm.getActivityLevel();
            item.stressLevel = hm.getStressLevel();
            item.bloodPressure = hm.getBloodPressure();
            item.notes = hm.getNotes();
            item.vetEntered = hm.getVetEntered();
            return item;
        }).collect(Collectors.toList());

        // Get reminders (pending and upcoming)
        List<HealthReminder> reminders = healthReminderRepository.findByPetOrderByReminderDateAsc(pet).stream()
                .filter(r -> "PENDING".equals(r.getStatus()))
                .limit(10)
                .collect(Collectors.toList());
        summary.reminders = reminders.stream().map(hr -> {
            PetHealthSummary.ReminderItem item = new PetHealthSummary.ReminderItem();
            item.id = hr.getId();
            item.title = hr.getTitle();
            item.description = hr.getDescription();
            item.reminderType = hr.getReminderType();
            item.reminderDate = hr.getReminderDate();
            item.status = hr.getStatus();
            return item;
        }).collect(Collectors.toList());

        return summary;
    }

    /**
     * Send appointment booked emails to both pet owner and doctor
     */
    private void sendAppointmentBookedEmails(Appointment appointment) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEE, MMM dd, yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

        String appointmentDate = appointment.getAppointmentDate().format(dateFormatter);
        String appointmentTime = appointment.getAppointmentTime() != null 
            ? appointment.getAppointmentTime().format(timeFormatter)
            : "To be scheduled";

        // Email to Pet Owner
        AppointmentEmailDTO petOwnerEmail = new AppointmentEmailDTO();
        petOwnerEmail.setAppointmentId(appointment.getId().toString());
        petOwnerEmail.setPetOwnerName(appointment.getPetOwner().getName());
        petOwnerEmail.setPetOwnerEmail(appointment.getPetOwner().getEmail());
        petOwnerEmail.setDoctorName(appointment.getDoctor().getName());
        petOwnerEmail.setPetName(appointment.getPet().getName());
        petOwnerEmail.setAppointmentDate(appointmentDate);
        petOwnerEmail.setAppointmentTime(appointmentTime);
        petOwnerEmail.setAppointmentStatus("Pending Approval");
        petOwnerEmail.setDashboardUrl(frontendUrl + "/dashboard");
        petOwnerEmail.setPetOwner(true);
        petOwnerEmail.setDoctor(false);

        emailService.sendAppointmentBookedEmail(petOwnerEmail);

        // Email to Doctor
        AppointmentEmailDTO doctorEmail = new AppointmentEmailDTO();
        doctorEmail.setAppointmentId(appointment.getId().toString());
        doctorEmail.setPetOwnerName(appointment.getPetOwner().getName());
        doctorEmail.setDoctorEmail(appointment.getDoctor().getEmail());
        doctorEmail.setDoctorName(appointment.getDoctor().getName());
        doctorEmail.setPetName(appointment.getPet().getName());
        doctorEmail.setAppointmentDate(appointmentDate);
        doctorEmail.setAppointmentTime(appointmentTime);
        doctorEmail.setAppointmentStatus("Pending Review");
        doctorEmail.setDashboardUrl(frontendUrl + "/dashboard");
        doctorEmail.setPetOwner(false);
        doctorEmail.setDoctor(true);

        emailService.sendAppointmentBookedEmail(doctorEmail);
    }

    /**
     * Send appointment status update email to pet owner
     */
    private void sendAppointmentStatusUpdateEmail(Appointment appointment, 
                                                   boolean isApproved, 
                                                   boolean isRejected, 
                                                   boolean isCompleted,
                                                   String doctorRemarks) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEE, MMM dd, yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

        String appointmentDate = appointment.getAppointmentDate().format(dateFormatter);
        String appointmentTime = appointment.getAppointmentTime() != null 
            ? appointment.getAppointmentTime().format(timeFormatter)
            : "To be confirmed";

        String statusEmoji = "📋";
        String statusTitle = "Status Update";
        
        if (isApproved) {
            statusEmoji = "✅";
            statusTitle = "Appointment Approved!";
        } else if (isRejected) {
            statusEmoji = "⚠️";
            statusTitle = "Appointment Declined";
        } else if (isCompleted) {
            statusEmoji = "✓";
            statusTitle = "Appointment Completed";
        }

        AppointmentEmailDTO emailData = new AppointmentEmailDTO();
        emailData.setAppointmentId(appointment.getId().toString());
        emailData.setPetOwnerName(appointment.getPetOwner().getName());
        emailData.setPetOwnerEmail(appointment.getPetOwner().getEmail());
        emailData.setDoctorName(appointment.getDoctor().getName());
        emailData.setPetName(appointment.getPet().getName());
        emailData.setAppointmentDate(appointmentDate);
        emailData.setAppointmentTime(appointmentTime);
        emailData.setAppointmentStatus(appointment.getStatus().toString());
        emailData.setStatusEmoji(statusEmoji);
        emailData.setStatusTitle(statusTitle);
        emailData.setApproved(isApproved);
        emailData.setRejected(isRejected);
        emailData.setCompleted(isCompleted);
        emailData.setDoctorRemarks(doctorRemarks);
        emailData.setHasRemarks(doctorRemarks != null && !doctorRemarks.isEmpty());
        emailData.setHasPrescription(false);
        emailData.setDashboardUrl(frontendUrl + "/dashboard");

        emailService.sendAppointmentStatusUpdateEmail(emailData);
    }
}
