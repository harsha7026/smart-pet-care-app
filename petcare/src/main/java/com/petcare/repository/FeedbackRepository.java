package com.petcare.repository;

import com.petcare.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    
    Optional<Feedback> findByAppointmentId(Long appointmentId);
    
    List<Feedback> findByDoctorId(Long doctorId);
    
    List<Feedback> findByPetOwnerId(Long petOwnerId);

    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.doctor.id = ?1")
    Double getAverageRatingForDoctor(Long doctorId);

    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.doctor.id = ?1")
    Long getFeedbackCountForDoctor(Long doctorId);
}
