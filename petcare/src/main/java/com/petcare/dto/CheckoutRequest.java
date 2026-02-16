package com.petcare.dto;

import java.math.BigDecimal;
import java.util.List;

public class CheckoutRequest {
    private List<CartItemRequest> items;
    private String shippingAddress;
    private BigDecimal totalAmount;

    public CheckoutRequest() {}

    public CheckoutRequest(List<CartItemRequest> items, String shippingAddress, BigDecimal totalAmount) {
        this.items = items;
        this.shippingAddress = shippingAddress;
        this.totalAmount = totalAmount;
    }

    public List<CartItemRequest> getItems() {
        return items;
    }

    public void setItems(List<CartItemRequest> items) {
        this.items = items;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
