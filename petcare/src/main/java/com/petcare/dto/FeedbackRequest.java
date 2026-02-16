package com.petcare.dto;

public class FeedbackRequest {
    private Long appointmentId;
    private Integer rating; // 1-5
    private String comments;

    public FeedbackRequest() {}

    public FeedbackRequest(Long appointmentId, Integer rating, String comments) {
        this.appointmentId = appointmentId;
        this.rating = rating;
        this.comments = comments;
    }

    public Long getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
}
