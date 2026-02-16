package com.petcare.dto;

public class PaymentEmailDTO {
    private String recipientEmail;
    private String recipientName;
    private String paymentId;
    private String transactionId;
    private String paymentDateTime;
    private String paymentDate;
    private String amount;
    private String paymentPurpose;
    private String paymentMethod;
    private String dashboardUrl;
    private String appointmentId;
    private String doctorName;
    private String appointmentDateTime;
    private String orderId;
    private String itemCount;
    private String shippingAddress;
    private String estimatedDelivery;
    private boolean isAppointmentPayment;
    private boolean isOrderPayment;

    // No-args constructor
    public PaymentEmailDTO() {
    }

    // Full constructor
    public PaymentEmailDTO(String recipientEmail, String recipientName, String paymentId,
                           String transactionId, String paymentDateTime, String paymentDate,
                           String amount, String paymentPurpose, String paymentMethod,
                           String dashboardUrl, String appointmentId, String doctorName,
                           String appointmentDateTime, String orderId, String itemCount,
                           String shippingAddress, String estimatedDelivery,
                           boolean isAppointmentPayment, boolean isOrderPayment) {
        this.recipientEmail = recipientEmail;
        this.recipientName = recipientName;
        this.paymentId = paymentId;
        this.transactionId = transactionId;
        this.paymentDateTime = paymentDateTime;
        this.paymentDate = paymentDate;
        this.amount = amount;
        this.paymentPurpose = paymentPurpose;
        this.paymentMethod = paymentMethod;
        this.dashboardUrl = dashboardUrl;
        this.appointmentId = appointmentId;
        this.doctorName = doctorName;
        this.appointmentDateTime = appointmentDateTime;
        this.orderId = orderId;
        this.itemCount = itemCount;
        this.shippingAddress = shippingAddress;
        this.estimatedDelivery = estimatedDelivery;
        this.isAppointmentPayment = isAppointmentPayment;
        this.isOrderPayment = isOrderPayment;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getPaymentDateTime() {
        return paymentDateTime;
    }

    public void setPaymentDateTime(String paymentDateTime) {
        this.paymentDateTime = paymentDateTime;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPaymentPurpose() {
        return paymentPurpose;
    }

    public void setPaymentPurpose(String paymentPurpose) {
        this.paymentPurpose = paymentPurpose;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getDashboardUrl() {
        return dashboardUrl;
    }

    public void setDashboardUrl(String dashboardUrl) {
        this.dashboardUrl = dashboardUrl;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getAppointmentDateTime() {
        return appointmentDateTime;
    }

    public void setAppointmentDateTime(String appointmentDateTime) {
        this.appointmentDateTime = appointmentDateTime;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getItemCount() {
        return itemCount;
    }

    public void setItemCount(String itemCount) {
        this.itemCount = itemCount;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getEstimatedDelivery() {
        return estimatedDelivery;
    }

    public void setEstimatedDelivery(String estimatedDelivery) {
        this.estimatedDelivery = estimatedDelivery;
    }

    public boolean isAppointmentPayment() {
        return isAppointmentPayment;
    }

    public void setAppointmentPayment(boolean appointmentPayment) {
        isAppointmentPayment = appointmentPayment;
    }

    public boolean isOrderPayment() {
        return isOrderPayment;
    }

    public void setOrderPayment(boolean orderPayment) {
        isOrderPayment = orderPayment;
    }
}
