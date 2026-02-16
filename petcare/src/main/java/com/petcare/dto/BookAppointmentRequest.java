package com.petcare.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class BookAppointmentRequest {
    public Long doctorId;
    public Long petId;
    public LocalDate appointmentDate;
    public LocalTime appointmentTime;
    public String reason;
    public BigDecimal fee;

    public BookAppointmentRequest() {}

    public BookAppointmentRequest(Long doctorId, Long petId, LocalDate appointmentDate, LocalTime appointmentTime, String reason, BigDecimal fee) {
        this.doctorId = doctorId;
        this.petId = petId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.reason = reason;
        this.fee = fee;
    }

    // Getters and Setters
    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }

    public Long getPetId() { return petId; }
    public void setPetId(Long petId) { this.petId = petId; }

    public LocalDate getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }

    public LocalTime getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(LocalTime appointmentTime) { this.appointmentTime = appointmentTime; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public BigDecimal getFee() { return fee; }
    public void setFee(BigDecimal fee) { this.fee = fee; }
}
