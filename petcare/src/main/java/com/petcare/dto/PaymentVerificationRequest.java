package com.petcare.dto;

public class PaymentVerificationRequest {
    public String razorpayOrderId;
    public String razorpayPaymentId;
    public String razorpaySignature;
    public Long appointmentId;

    public PaymentVerificationRequest() {}

    public PaymentVerificationRequest(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature, Long appointmentId) {
        this.razorpayOrderId = razorpayOrderId;
        this.razorpayPaymentId = razorpayPaymentId;
        this.razorpaySignature = razorpaySignature;
        this.appointmentId = appointmentId;
    }

    // Getters and Setters
    public String getRazorpayOrderId() { return razorpayOrderId; }
    public void setRazorpayOrderId(String razorpayOrderId) { this.razorpayOrderId = razorpayOrderId; }

    public String getRazorpayPaymentId() { return razorpayPaymentId; }
    public void setRazorpayPaymentId(String razorpayPaymentId) { this.razorpayPaymentId = razorpayPaymentId; }

    public String getRazorpaySignature() { return razorpaySignature; }
    public void setRazorpaySignature(String razorpaySignature) { this.razorpaySignature = razorpaySignature; }

    public Long getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }
}
