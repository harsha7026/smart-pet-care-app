package com.petcare.model;

public enum OrderStatus {
    PLACED,              // ORDER_PLACED
    PAYMENT_CONFIRMED,   // PAYMENT_CONFIRMED
    PACKED,              // ORDER_PACKED
    SHIPPED,             // SHIPPED
    OUT_FOR_DELIVERY,    // OUT_FOR_DELIVERY
    DELIVERED,           // DELIVERED
    CANCELLED            // CANCELLED
}
