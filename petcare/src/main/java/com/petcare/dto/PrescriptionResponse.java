package com.petcare.dto;

import java.time.LocalDateTime;

public class PrescriptionResponse {
    private Long id;
    private Long appointmentId;
    private String doctorName;
    private String petName;
    private String diagnosis;
    private String medicines;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime appointmentDate;

    public PrescriptionResponse() {
    }

    public PrescriptionResponse(Long id, Long appointmentId, String doctorName, String petName,
                                String diagnosis, String medicines, String notes,
                                LocalDateTime createdAt, LocalDateTime appointmentDate) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.doctorName = doctorName;
        this.petName = petName;
        this.diagnosis = diagnosis;
        this.medicines = medicines;
        this.notes = notes;
        this.createdAt = createdAt;
        this.appointmentDate = appointmentDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getMedicines() {
        return medicines;
    }

    public void setMedicines(String medicines) {
        this.medicines = medicines;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDateTime appointmentDate) {
        this.appointmentDate = appointmentDate;
    }
}

