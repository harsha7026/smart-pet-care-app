package com.petcare.dto;

public class OrderPaymentConfirmation {
    private String razorpayPaymentId;
    private String razorpayOrderId;
    private String razorpaySignature;

    public OrderPaymentConfirmation() {}

    public OrderPaymentConfirmation(String razorpayPaymentId, String razorpayOrderId, String razorpaySignature) {
        this.razorpayPaymentId = razorpayPaymentId;
        this.razorpayOrderId = razorpayOrderId;
        this.razorpaySignature = razorpaySignature;
    }

    public String getRazorpayPaymentId() {
        return razorpayPaymentId;
    }

    public void setRazorpayPaymentId(String razorpayPaymentId) {
        this.razorpayPaymentId = razorpayPaymentId;
    }

    public String getRazorpayOrderId() {
        return razorpayOrderId;
    }

    public void setRazorpayOrderId(String razorpayOrderId) {
        this.razorpayOrderId = razorpayOrderId;
    }

    public String getRazorpaySignature() {
        return razorpaySignature;
    }

    public void setRazorpaySignature(String razorpaySignature) {
        this.razorpaySignature = razorpaySignature;
    }
}
