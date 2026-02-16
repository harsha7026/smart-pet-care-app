package com.petcare.dto;

import java.time.LocalDateTime;

public class SupportMessageDTO {
    private Long id;
    private Long ticketId;
    private Long senderId;
    private String senderName;
    private String senderRole;
    private String message;
    private LocalDateTime timestamp;
    private boolean isAdmin;

    public SupportMessageDTO() {}

    public SupportMessageDTO(Long id, Long ticketId, Long senderId, String senderName, 
                            String senderRole, String message, LocalDateTime timestamp, boolean isAdmin) {
        this.id = id;
        this.ticketId = ticketId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderRole = senderRole;
        this.message = message;
        this.timestamp = timestamp;
        this.isAdmin = isAdmin;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderRole() {
        return senderRole;
    }

    public void setSenderRole(String senderRole) {
        this.senderRole = senderRole;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
