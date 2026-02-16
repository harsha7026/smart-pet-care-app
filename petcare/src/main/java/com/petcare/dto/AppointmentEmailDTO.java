package com.petcare.dto;

import java.time.LocalDateTime;

/**
 * DTO for Appointment Booked Email
 */
public class AppointmentEmailDTO {
    private String appointmentId;
    private String petOwnerName;
    private String petOwnerEmail;
    private String doctorName;
    private String doctorEmail;
    private String petName;
    private LocalDateTime appointmentDateTime;
    private String appointmentStatus;
    private String dashboardUrl;
    private boolean isPetOwner;
    private boolean isDoctor;
    
    // For status update email
    private String statusEmoji;
    private String statusTitle;
    private String appointmentDate;
    private String appointmentTime;
    private boolean isApproved;
    private boolean isRejected;
    private boolean isCompleted;
    private String doctorRemarks;
    private boolean hasRemarks;
    private String prescriptionUrl;
    private boolean hasPrescription;

    // Constructors
    public AppointmentEmailDTO() {}

    // Getters and Setters
    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }

    public String getPetOwnerName() { return petOwnerName; }
    public void setPetOwnerName(String petOwnerName) { this.petOwnerName = petOwnerName; }

    public String getPetOwnerEmail() { return petOwnerEmail; }
    public void setPetOwnerEmail(String petOwnerEmail) { this.petOwnerEmail = petOwnerEmail; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public String getDoctorEmail() { return doctorEmail; }
    public void setDoctorEmail(String doctorEmail) { this.doctorEmail = doctorEmail; }

    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }

    public LocalDateTime getAppointmentDateTime() { return appointmentDateTime; }
    public void setAppointmentDateTime(LocalDateTime appointmentDateTime) { this.appointmentDateTime = appointmentDateTime; }

    public String getAppointmentStatus() { return appointmentStatus; }
    public void setAppointmentStatus(String appointmentStatus) { this.appointmentStatus = appointmentStatus; }

    public String getDashboardUrl() { return dashboardUrl; }
    public void setDashboardUrl(String dashboardUrl) { this.dashboardUrl = dashboardUrl; }

    public boolean isPetOwner() { return isPetOwner; }
    public void setPetOwner(boolean petOwner) { isPetOwner = petOwner; }

    public boolean isDoctor() { return isDoctor; }
    public void setDoctor(boolean doctor) { isDoctor = doctor; }

    public String getStatusEmoji() { return statusEmoji; }
    public void setStatusEmoji(String statusEmoji) { this.statusEmoji = statusEmoji; }

    public String getStatusTitle() { return statusTitle; }
    public void setStatusTitle(String statusTitle) { this.statusTitle = statusTitle; }

    public String getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(String appointmentDate) { this.appointmentDate = appointmentDate; }

    public String getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(String appointmentTime) { this.appointmentTime = appointmentTime; }

    public boolean isApproved() { return isApproved; }
    public void setApproved(boolean approved) { isApproved = approved; }

    public boolean isRejected() { return isRejected; }
    public void setRejected(boolean rejected) { isRejected = rejected; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

    public String getDoctorRemarks() { return doctorRemarks; }
    public void setDoctorRemarks(String doctorRemarks) { this.doctorRemarks = doctorRemarks; }

    public boolean isHasRemarks() { return hasRemarks; }
    public void setHasRemarks(boolean hasRemarks) { this.hasRemarks = hasRemarks; }

    public String getPrescriptionUrl() { return prescriptionUrl; }
    public void setPrescriptionUrl(String prescriptionUrl) { this.prescriptionUrl = prescriptionUrl; }

    public boolean isHasPrescription() { return hasPrescription; }
    public void setHasPrescription(boolean hasPrescription) { this.hasPrescription = hasPrescription; }
}
