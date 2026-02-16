package com.petcare.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NotificationType type;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "related_entity_id")
    private Long relatedEntityId;

    @Column(name = "related_entity_type", length = 50)
    private String relatedEntityType; // "APPOINTMENT", "PRESCRIPTION", etc.

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructors
    public Notification() {
    }

    public Notification(Long id, Long userId, NotificationType type, String title, String message, 
                       Boolean isRead, Long relatedEntityId, String relatedEntityType, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.message = message;
        this.isRead = isRead;
        this.relatedEntityId = relatedEntityId;
        this.relatedEntityType = relatedEntityType;
        this.createdAt = createdAt;
    }

    // Builder pattern
    public static NotificationBuilder builder() {
        return new NotificationBuilder();
    }

    public static class NotificationBuilder {
        private Long userId;
        private NotificationType type;
        private String title;
        private String message;
        private Boolean isRead = false;
        private Long relatedEntityId;
        private String relatedEntityType;

        NotificationBuilder() {}

        public NotificationBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public NotificationBuilder type(NotificationType type) {
            this.type = type;
            return this;
        }

        public NotificationBuilder title(String title) {
            this.title = title;
            return this;
        }

        public NotificationBuilder message(String message) {
            this.message = message;
            return this;
        }

        public NotificationBuilder isRead(Boolean isRead) {
            this.isRead = isRead;
            return this;
        }

        public NotificationBuilder relatedEntityId(Long relatedEntityId) {
            this.relatedEntityId = relatedEntityId;
            return this;
        }

        public NotificationBuilder relatedEntityType(String relatedEntityType) {
            this.relatedEntityType = relatedEntityType;
            return this;
        }

        public Notification build() {
            Notification notification = new Notification();
            notification.userId = this.userId;
            notification.type = this.type;
            notification.title = this.title;
            notification.message = this.message;
            notification.isRead = this.isRead;
            notification.relatedEntityId = this.relatedEntityId;
            notification.relatedEntityType = this.relatedEntityType;
            return notification;
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public Long getRelatedEntityId() {
        return relatedEntityId;
    }

    public void setRelatedEntityId(Long relatedEntityId) {
        this.relatedEntityId = relatedEntityId;
    }

    public String getRelatedEntityType() {
        return relatedEntityType;
    }

    public void setRelatedEntityType(String relatedEntityType) {
        this.relatedEntityType = relatedEntityType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
