package com.petcare.dto;

import com.petcare.model.AppointmentStatus;
import com.petcare.model.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class AppointmentResponse {
    public Long id;
    public Long doctorId;
    public String doctorName;
    public Long petOwnerId;
    public String petOwnerName;
    public Long petId;
    public String petName;
    public LocalDate appointmentDate;
    public LocalTime appointmentTime;
    public String reason;
    public BigDecimal fee;
    public AppointmentStatus status;
    public PaymentStatus paymentStatus;
    public String razorpayOrderId;
    public String razorpayPaymentId;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

    public AppointmentResponse() {}

    public AppointmentResponse(Long id, Long doctorId, String doctorName, Long petOwnerId, 
                              String petOwnerName, Long petId, String petName, LocalDate appointmentDate, 
                              LocalTime appointmentTime, String reason, BigDecimal fee, 
                              AppointmentStatus status, PaymentStatus paymentStatus, 
                              String razorpayOrderId, String razorpayPaymentId, 
                              LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.petOwnerId = petOwnerId;
        this.petOwnerName = petOwnerName;
        this.petId = petId;
        this.petName = petName;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.reason = reason;
        this.fee = fee;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.razorpayOrderId = razorpayOrderId;
        this.razorpayPaymentId = razorpayPaymentId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public Long getPetOwnerId() { return petOwnerId; }
    public void setPetOwnerId(Long petOwnerId) { this.petOwnerId = petOwnerId; }

    public String getPetOwnerName() { return petOwnerName; }
    public void setPetOwnerName(String petOwnerName) { this.petOwnerName = petOwnerName; }

    public Long getPetId() { return petId; }
    public void setPetId(Long petId) { this.petId = petId; }

    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }

    public LocalDate getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }

    public LocalTime getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(LocalTime appointmentTime) { this.appointmentTime = appointmentTime; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public BigDecimal getFee() { return fee; }
    public void setFee(BigDecimal fee) { this.fee = fee; }

    public AppointmentStatus getStatus() { return status; }
    public void setStatus(AppointmentStatus status) { this.status = status; }

    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getRazorpayOrderId() { return razorpayOrderId; }
    public void setRazorpayOrderId(String razorpayOrderId) { this.razorpayOrderId = razorpayOrderId; }

    public String getRazorpayPaymentId() { return razorpayPaymentId; }
    public void setRazorpayPaymentId(String razorpayPaymentId) { this.razorpayPaymentId = razorpayPaymentId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
