package com.petcare.service;

import com.petcare.dto.PrescriptionRequest;
import com.petcare.dto.PrescriptionResponse;
import com.petcare.entity.Appointment;
import com.petcare.entity.Prescription;
import com.petcare.entity.User;
import com.petcare.model.AppointmentStatus;
import com.petcare.repository.AppointmentRepository;
import com.petcare.repository.PrescriptionRepository;
import com.petcare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PrescriptionService {

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public PrescriptionResponse createPrescription(Long doctorId, PrescriptionRequest request) {
        // Validate appointment exists
        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // Validate appointment is approved or completed
        if (appointment.getStatus() != AppointmentStatus.APPROVED && 
            appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new RuntimeException("Prescription can only be added for approved or completed appointments");
        }

        // Validate doctor is the assigned doctor
        if (!appointment.getDoctor().getId().equals(doctorId)) {
            throw new RuntimeException("You are not authorized to add prescription for this appointment");
        }

        // Check if prescription already exists
        if (prescriptionRepository.existsByAppointmentId(request.getAppointmentId())) {
            throw new RuntimeException("Prescription already exists for this appointment");
        }

        // Create prescription
        Prescription prescription = new Prescription();
        prescription.setAppointment(appointment);
        prescription.setDoctor(appointment.getDoctor());
        prescription.setPet(appointment.getPet());
        prescription.setDiagnosis(request.getDiagnosis());
        prescription.setMedicines(request.getMedicines());
        prescription.setNotes(request.getNotes());

        Prescription saved = prescriptionRepository.save(prescription);

        return mapToResponse(saved);
    }

    public PrescriptionResponse getPrescriptionByAppointmentId(Long appointmentId) {
        Prescription prescription = prescriptionRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new RuntimeException("Prescription not found for this appointment"));

        return mapToResponse(prescription);
    }

    private PrescriptionResponse mapToResponse(Prescription prescription) {
        PrescriptionResponse response = new PrescriptionResponse();
        response.setId(prescription.getId());
        response.setAppointmentId(prescription.getAppointment().getId());
        response.setDoctorName(prescription.getDoctor().getName());
        response.setPetName(prescription.getPet().getName());
        response.setDiagnosis(prescription.getDiagnosis());
        response.setMedicines(prescription.getMedicines());
        response.setNotes(prescription.getNotes());
        response.setCreatedAt(prescription.getCreatedAt());
        response.setAppointmentDate(prescription.getAppointment().getAppointmentDateTime());
        return response;
    }
}
