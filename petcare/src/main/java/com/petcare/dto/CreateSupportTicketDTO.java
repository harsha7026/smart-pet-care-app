package com.petcare.dto;

public class CreateSupportTicketDTO {
    private String category;
    private String subject;
    private String description;

    public CreateSupportTicketDTO() {}

    public CreateSupportTicketDTO(String category, String subject, String description) {
        this.category = category;
        this.subject = subject;
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
