package com.petcare.dto;

public class DoctorVerificationEmailDTO {
    private String recipientEmail;
    private String doctorName;
    private String specialization;
    private String clinicName;
    private String verificationDate;
    private String dashboardUrl;
    private boolean isApproved;
    private boolean isRejected;
    private String rejectionReason;
    private String statusClass;

    // No-args constructor
    public DoctorVerificationEmailDTO() {
    }

    // Full constructor
    public DoctorVerificationEmailDTO(String recipientEmail, String doctorName,
                                      String specialization, String clinicName,
                                      String verificationDate, String dashboardUrl,
                                      boolean isApproved, boolean isRejected,
                                      String rejectionReason, String statusClass) {
        this.recipientEmail = recipientEmail;
        this.doctorName = doctorName;
        this.specialization = specialization;
        this.clinicName = clinicName;
        this.verificationDate = verificationDate;
        this.dashboardUrl = dashboardUrl;
        this.isApproved = isApproved;
        this.isRejected = isRejected;
        this.rejectionReason = rejectionReason;
        this.statusClass = statusClass;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public String getVerificationDate() {
        return verificationDate;
    }

    public void setVerificationDate(String verificationDate) {
        this.verificationDate = verificationDate;
    }

    public String getDashboardUrl() {
        return dashboardUrl;
    }

    public void setDashboardUrl(String dashboardUrl) {
        this.dashboardUrl = dashboardUrl;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public boolean isRejected() {
        return isRejected;
    }

    public void setRejected(boolean rejected) {
        isRejected = rejected;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getStatusClass() {
        return statusClass;
    }

    public void setStatusClass(String statusClass) {
        this.statusClass = statusClass;
    }
}
