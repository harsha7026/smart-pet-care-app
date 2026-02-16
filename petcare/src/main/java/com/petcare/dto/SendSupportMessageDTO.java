package com.petcare.dto;

public class SendSupportMessageDTO {
    private Long ticketId;
    private String message;

    public SendSupportMessageDTO() {}

    public SendSupportMessageDTO(Long ticketId, String message) {
        this.ticketId = ticketId;
        this.message = message;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
