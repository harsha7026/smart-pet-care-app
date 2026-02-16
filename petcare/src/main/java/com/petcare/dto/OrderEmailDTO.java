package com.petcare.dto;

import java.util.List;

public class OrderEmailDTO {
    private String recipientEmail;
    private String recipientName;
    private String orderId;
    private String orderDate;
    private String orderStatus;
    private String subtotal;
    private String shipping;
    private String tax;
    private String totalAmount;
    private String trackingUrl;
    private String trackingNumber;
    private String shippingAddress;
    private String estimatedDelivery;
    private String dashboardUrl;
    private List<OrderItemDTO> items;
    private boolean hasMoreItems;
    private String moreItemsCount;
    private String statusEmoji;
    private String statusTitle;
    private String placedDate;
    private String processingDate;
    private String shippedDate;
    private String outForDeliveryDate;
    private String deliveredDate;
    private boolean isProcessing;
    private boolean isShipped;
    private boolean isOutForDelivery;
    private boolean isDelivered;
    private boolean statusAfterPlaced;
    private boolean statusAfterShipped;
    private boolean statusAfterOutForDelivery;
    private boolean statusAfterDelivered;

    // No-args constructor
    public OrderEmailDTO() {
    }

    // Full constructor
    public OrderEmailDTO(String recipientEmail, String recipientName, String orderId,
                         String orderDate, String orderStatus, String subtotal,
                         String shipping, String tax, String totalAmount,
                         String trackingUrl, String trackingNumber, String shippingAddress,
                         String estimatedDelivery, String dashboardUrl, List<OrderItemDTO> items,
                         boolean hasMoreItems, String moreItemsCount, String statusEmoji,
                         String statusTitle, String placedDate, String processingDate,
                         String shippedDate, String outForDeliveryDate, String deliveredDate,
                         boolean isProcessing, boolean isShipped, boolean isOutForDelivery,
                         boolean isDelivered, boolean statusAfterPlaced, boolean statusAfterShipped,
                         boolean statusAfterOutForDelivery, boolean statusAfterDelivered) {
        this.recipientEmail = recipientEmail;
        this.recipientName = recipientName;
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.subtotal = subtotal;
        this.shipping = shipping;
        this.tax = tax;
        this.totalAmount = totalAmount;
        this.trackingUrl = trackingUrl;
        this.trackingNumber = trackingNumber;
        this.shippingAddress = shippingAddress;
        this.estimatedDelivery = estimatedDelivery;
        this.dashboardUrl = dashboardUrl;
        this.items = items;
        this.hasMoreItems = hasMoreItems;
        this.moreItemsCount = moreItemsCount;
        this.statusEmoji = statusEmoji;
        this.statusTitle = statusTitle;
        this.placedDate = placedDate;
        this.processingDate = processingDate;
        this.shippedDate = shippedDate;
        this.outForDeliveryDate = outForDeliveryDate;
        this.deliveredDate = deliveredDate;
        this.isProcessing = isProcessing;
        this.isShipped = isShipped;
        this.isOutForDelivery = isOutForDelivery;
        this.isDelivered = isDelivered;
        this.statusAfterPlaced = statusAfterPlaced;
        this.statusAfterShipped = statusAfterShipped;
        this.statusAfterOutForDelivery = statusAfterOutForDelivery;
        this.statusAfterDelivered = statusAfterDelivered;
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

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    public String getShipping() {
        return shipping;
    }

    public void setShipping(String shipping) {
        this.shipping = shipping;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getTrackingUrl() {
        return trackingUrl;
    }

    public void setTrackingUrl(String trackingUrl) {
        this.trackingUrl = trackingUrl;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
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

    public String getDashboardUrl() {
        return dashboardUrl;
    }

    public void setDashboardUrl(String dashboardUrl) {
        this.dashboardUrl = dashboardUrl;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }

    public boolean isHasMoreItems() {
        return hasMoreItems;
    }

    public void setHasMoreItems(boolean hasMoreItems) {
        this.hasMoreItems = hasMoreItems;
    }

    public String getMoreItemsCount() {
        return moreItemsCount;
    }

    public void setMoreItemsCount(String moreItemsCount) {
        this.moreItemsCount = moreItemsCount;
    }

    public String getStatusEmoji() {
        return statusEmoji;
    }

    public void setStatusEmoji(String statusEmoji) {
        this.statusEmoji = statusEmoji;
    }

    public String getStatusTitle() {
        return statusTitle;
    }

    public void setStatusTitle(String statusTitle) {
        this.statusTitle = statusTitle;
    }

    public String getPlacedDate() {
        return placedDate;
    }

    public void setPlacedDate(String placedDate) {
        this.placedDate = placedDate;
    }

    public String getProcessingDate() {
        return processingDate;
    }

    public void setProcessingDate(String processingDate) {
        this.processingDate = processingDate;
    }

    public String getShippedDate() {
        return shippedDate;
    }

    public void setShippedDate(String shippedDate) {
        this.shippedDate = shippedDate;
    }

    public String getOutForDeliveryDate() {
        return outForDeliveryDate;
    }

    public void setOutForDeliveryDate(String outForDeliveryDate) {
        this.outForDeliveryDate = outForDeliveryDate;
    }

    public String getDeliveredDate() {
        return deliveredDate;
    }

    public void setDeliveredDate(String deliveredDate) {
        this.deliveredDate = deliveredDate;
    }

    public boolean isProcessing() {
        return isProcessing;
    }

    public void setProcessing(boolean processing) {
        isProcessing = processing;
    }

    public boolean isShipped() {
        return isShipped;
    }

    public void setShipped(boolean shipped) {
        isShipped = shipped;
    }

    public boolean isOutForDelivery() {
        return isOutForDelivery;
    }

    public void setOutForDelivery(boolean outForDelivery) {
        isOutForDelivery = outForDelivery;
    }

    public boolean isDelivered() {
        return isDelivered;
    }

    public void setDelivered(boolean delivered) {
        isDelivered = delivered;
    }

    public boolean isStatusAfterPlaced() {
        return statusAfterPlaced;
    }

    public void setStatusAfterPlaced(boolean statusAfterPlaced) {
        this.statusAfterPlaced = statusAfterPlaced;
    }

    public boolean isStatusAfterShipped() {
        return statusAfterShipped;
    }

    public void setStatusAfterShipped(boolean statusAfterShipped) {
        this.statusAfterShipped = statusAfterShipped;
    }

    public boolean isStatusAfterOutForDelivery() {
        return statusAfterOutForDelivery;
    }

    public void setStatusAfterOutForDelivery(boolean statusAfterOutForDelivery) {
        this.statusAfterOutForDelivery = statusAfterOutForDelivery;
    }

    public boolean isStatusAfterDelivered() {
        return statusAfterDelivered;
    }

    public void setStatusAfterDelivered(boolean statusAfterDelivered) {
        this.statusAfterDelivered = statusAfterDelivered;
    }
}
