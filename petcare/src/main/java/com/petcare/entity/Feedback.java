package com.petcare.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "feedbacks", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"appointment_id"}, name = "uk_feedback_appointment")
})
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false, foreignKey = @ForeignKey(name = "fk_feedback_appointment"))
    @JsonIgnore
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false, foreignKey = @ForeignKey(name = "fk_feedback_doctor"))
    @JsonIgnore
    private User doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_owner_id", nullable = false, foreignKey = @ForeignKey(name = "fk_feedback_pet_owner"))
    @JsonIgnore
    private User petOwner;

    @Column(nullable = false)
    private Integer rating; // 1-5 stars

    @Column(length = 1000)
    private String comments;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Feedback() {}

    public Feedback(Long id, Appointment appointment, User doctor, User petOwner, Integer rating, String comments, LocalDateTime createdAt) {
        this.id = id;
        this.appointment = appointment;
        this.doctor = doctor;
        this.petOwner = petOwner;
        this.rating = rating;
        this.comments = comments;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Appointment getAppointment() { return appointment; }
    public void setAppointment(Appointment appointment) { this.appointment = appointment; }

    public User getDoctor() { return doctor; }
    public void setDoctor(User doctor) { this.doctor = doctor; }

    public User getPetOwner() { return petOwner; }
    public void setPetOwner(User petOwner) { this.petOwner = petOwner; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
