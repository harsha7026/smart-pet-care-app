package com.petcare.dto;

public class OrderItemDTO {
    private String productName;
    private String quantity;
    private String itemPrice;
    private String itemTotal;

    // No-args constructor
    public OrderItemDTO() {
    }

    // Full constructor
    public OrderItemDTO(String productName, String quantity, String itemPrice, String itemTotal) {
        this.productName = productName;
        this.quantity = quantity;
        this.itemPrice = itemPrice;
        this.itemTotal = itemTotal;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemTotal() {
        return itemTotal;
    }

    public void setItemTotal(String itemTotal) {
        this.itemTotal = itemTotal;
    }
}
