package com.petcare.controller;

import com.petcare.dto.NotificationDTO;
import com.petcare.entity.User;
import com.petcare.repository.UserRepository;
import com.petcare.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public NotificationController(NotificationService notificationService, UserRepository userRepository) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    private Long resolveUserId(HttpSession session) {
        Object userId = session.getAttribute("userId");
        if (userId instanceof Long) {
            return (Long) userId;
        }
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            String email = auth.getName();
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                return userOpt.get().getId();
            }
        }
        
        return null;
    }

    /**
     * GET /api/notifications - Get all notifications for current user
     */
    @GetMapping
    public ResponseEntity<?> getUserNotifications(HttpSession session) {
        try {
            Long userId = resolveUserId(session);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not authenticated"));
            }
            List<NotificationDTO> notifications = notificationService.getUserNotifications(userId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/notifications/recent - Get recent notifications (last 10)
     */
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentNotifications(HttpSession session) {
        try {
            Long userId = resolveUserId(session);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not authenticated"));
            }
            List<NotificationDTO> notifications = notificationService.getRecentNotifications(userId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/notifications/unread-count - Get count of unread notifications
     */
    @GetMapping("/unread-count")
    public ResponseEntity<?> getUnreadCount(HttpSession session) {
        try {
            Long userId = resolveUserId(session);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not authenticated"));
            }
            Long count = notificationService.getUnreadCount(userId);
            Map<String, Long> response = new HashMap<>();
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/notifications/unread - Get unread notifications
     */
    @GetMapping("/unread")
    public ResponseEntity<?> getUnreadNotifications(HttpSession session) {
        try {
            Long userId = resolveUserId(session);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not authenticated"));
            }
            List<NotificationDTO> notifications = notificationService.getUnreadNotifications(userId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /api/notifications/{id}/read - Mark notification as read
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(
            @PathVariable Long id,
            HttpSession session) {
        try {
            Long userId = resolveUserId(session);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not authenticated"));
            }
            notificationService.markAsRead(id, userId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Notification marked as read");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /api/notifications/read-all - Mark all notifications as read
     */
    @PutMapping("/read-all")
    public ResponseEntity<?> markAllAsRead(HttpSession session) {
        try {
            Long userId = resolveUserId(session);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not authenticated"));
            }
            notificationService.markAllAsRead(userId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "All notifications marked as read");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/notifications/{id} - Delete a notification
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(
            @PathVariable Long id,
            HttpSession session) {
        try {
            Long userId = resolveUserId(session);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not authenticated"));
            }
            notificationService.deleteNotification(id, userId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Notification deleted");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
}
