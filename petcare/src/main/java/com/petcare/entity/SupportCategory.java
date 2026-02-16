package com.petcare.entity;

public enum SupportCategory {
    APPOINTMENT("Appointment Issues"),
    PAYMENT("Payment Issues"),
    ORDER("Order Issues"),
    ACCOUNT("Account Issues"),
    TECHNICAL("Technical Issues"),
    OTHER("Other");

    private final String displayName;

    SupportCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
