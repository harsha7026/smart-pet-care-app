package com.petcare.service;

import com.petcare.dto.FeedbackRequest;
import com.petcare.entity.Appointment;
import com.petcare.entity.Feedback;
import com.petcare.entity.User;
import com.petcare.repository.AppointmentRepository;
import com.petcare.repository.FeedbackRepository;
import com.petcare.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    public FeedbackService(FeedbackRepository feedbackRepository, 
                          AppointmentRepository appointmentRepository,
                          UserRepository userRepository) {
        this.feedbackRepository = feedbackRepository;
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
    }

    public Feedback submitFeedback(FeedbackRequest request, Long petOwnerId) {
        // Validate rating
        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        // Get appointment
        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
            .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // Verify pet owner
        if (!appointment.getPetOwner().getId().equals(petOwnerId)) {
            throw new RuntimeException("Unauthorized: You can only submit feedback for your own appointments");
        }

        // Check if feedback already exists
        if (feedbackRepository.findByAppointmentId(request.getAppointmentId()).isPresent()) {
            throw new RuntimeException("Feedback already submitted for this appointment");
        }

        // Create feedback
        Feedback feedback = new Feedback();
        feedback.setAppointment(appointment);
        feedback.setDoctor(appointment.getDoctor());
        feedback.setPetOwner(appointment.getPetOwner());
        feedback.setRating(request.getRating());
        feedback.setComments(request.getComments());

        return feedbackRepository.save(feedback);
    }

    public Optional<Feedback> getFeedbackByAppointmentId(Long appointmentId) {
        return feedbackRepository.findByAppointmentId(appointmentId);
    }

    public List<Feedback> getFeedbackForDoctor(Long doctorId) {
        return feedbackRepository.findByDoctorId(doctorId);
    }

    public List<Feedback> getFeedbackFromUser(Long petOwnerId) {
        return feedbackRepository.findByPetOwnerId(petOwnerId);
    }

    public Double getAverageRatingForDoctor(Long doctorId) {
        Double average = feedbackRepository.getAverageRatingForDoctor(doctorId);
        return average != null ? Math.round(average * 10.0) / 10.0 : 0.0;
    }

    public Long getFeedbackCountForDoctor(Long doctorId) {
        return feedbackRepository.getFeedbackCountForDoctor(doctorId);
    }

    public Feedback updateFeedback(Long feedbackId, FeedbackRequest request, Long petOwnerId) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
            .orElseThrow(() -> new RuntimeException("Feedback not found"));

        if (!feedback.getPetOwner().getId().equals(petOwnerId)) {
            throw new RuntimeException("Unauthorized: You can only update your own feedback");
        }

        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        feedback.setRating(request.getRating());
        feedback.setComments(request.getComments());

        return feedbackRepository.save(feedback);
    }

    public void deleteFeedback(Long feedbackId, Long petOwnerId) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
            .orElseThrow(() -> new RuntimeException("Feedback not found"));

        if (!feedback.getPetOwner().getId().equals(petOwnerId)) {
            throw new RuntimeException("Unauthorized: You can only delete your own feedback");
        }

        feedbackRepository.deleteById(feedbackId);
    }
}
