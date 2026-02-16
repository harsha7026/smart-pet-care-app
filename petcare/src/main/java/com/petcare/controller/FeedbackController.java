package com.petcare.controller;

import com.petcare.dto.FeedbackRequest;
import com.petcare.entity.Feedback;
import com.petcare.service.FeedbackService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    private Long getUserIdFromSession(HttpSession session) {
        Object userId = session.getAttribute("userId");
        if (userId instanceof Long) {
            return (Long) userId;
        }
        return null;
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitFeedback(@RequestBody FeedbackRequest request, HttpSession session) {
        try {
            Long userId = getUserIdFromSession(session);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not authenticated"));
            }

            Feedback feedback = feedbackService.submitFeedback(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(feedback);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to submit feedback"));
        }
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<?> getFeedbackByAppointment(@PathVariable Long appointmentId) {
        try {
            return feedbackService.getFeedbackByAppointmentId(appointmentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<?> getDoctorFeedback(@PathVariable Long doctorId) {
        try {
            List<Feedback> feedbacks = feedbackService.getFeedbackForDoctor(doctorId);
            Double averageRating = feedbackService.getAverageRatingForDoctor(doctorId);
            Long feedbackCount = feedbackService.getFeedbackCountForDoctor(doctorId);

            return ResponseEntity.ok(Map.of(
                "feedbacks", feedbacks,
                "averageRating", averageRating,
                "feedbackCount", feedbackCount
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/doctor/{doctorId}/rating-summary")
    public ResponseEntity<?> getDoctorRatingSummary(@PathVariable Long doctorId) {
        try {
            Double averageRating = feedbackService.getAverageRatingForDoctor(doctorId);
            Long feedbackCount = feedbackService.getFeedbackCountForDoctor(doctorId);

            return ResponseEntity.ok(Map.of(
                "averageRating", averageRating,
                "feedbackCount", feedbackCount
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/my-feedback")
    public ResponseEntity<?> getMyFeedback(HttpSession session) {
        try {
            Long userId = getUserIdFromSession(session);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not authenticated"));
            }

            List<Feedback> feedbacks = feedbackService.getFeedbackFromUser(userId);
            return ResponseEntity.ok(feedbacks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{feedbackId}")
    public ResponseEntity<?> updateFeedback(@PathVariable Long feedbackId,
                                           @RequestBody FeedbackRequest request,
                                           HttpSession session) {
        try {
            Long userId = getUserIdFromSession(session);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not authenticated"));
            }

            Feedback feedback = feedbackService.updateFeedback(feedbackId, request, userId);
            return ResponseEntity.ok(feedback);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to update feedback"));
        }
    }

    @DeleteMapping("/{feedbackId}")
    public ResponseEntity<?> deleteFeedback(@PathVariable Long feedbackId, HttpSession session) {
        try {
            Long userId = getUserIdFromSession(session);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not authenticated"));
            }

            feedbackService.deleteFeedback(feedbackId, userId);
            return ResponseEntity.ok(Map.of("message", "Feedback deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to delete feedback"));
        }
    }
}
