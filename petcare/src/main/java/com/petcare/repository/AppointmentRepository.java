package com.petcare.repository;

import com.petcare.entity.Appointment;
import com.petcare.entity.User;
import com.petcare.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDoctor(User doctor);
    List<Appointment> findByPetOwner(User petOwner);
    List<Appointment> findByStatus(AppointmentStatus status);
    List<Appointment> findByDoctorAndStatus(User doctor, AppointmentStatus status);
    List<Appointment> findByPetOwnerId(Long petOwnerId);
    List<Appointment> findByDoctorIdAndAppointmentDateTimeGreaterThanEqual(Long doctorId, LocalDateTime dateTime);
    List<Appointment> findByPetId(Long petId);

    // New methods for Milestone 2
    List<Appointment> findByPetOwnerIdOrderByAppointmentDateDesc(Long petOwnerId);

    List<Appointment> findByDoctorIdOrderByAppointmentDateDesc(Long doctorId);

    List<Appointment> findByDoctorIdAndStatusOrderByAppointmentDateDesc(Long doctorId, AppointmentStatus status);

    List<Appointment> findByPetOwnerIdAndStatusOrderByAppointmentDateDesc(Long petOwnerId, AppointmentStatus status);

    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDate >= CURRENT_DATE ORDER BY a.appointmentDate ASC, a.appointmentTime ASC")
    List<Appointment> findUpcomingAppointmentsByDoctor(@Param("doctorId") Long doctorId);

    @Query("SELECT a FROM Appointment a WHERE a.petOwner.id = :petOwnerId AND a.appointmentDate >= CURRENT_DATE ORDER BY a.appointmentDate ASC, a.appointmentTime ASC")
    List<Appointment> findUpcomingAppointmentsByPetOwner(@Param("petOwnerId") Long petOwnerId);

    Optional<Appointment> findByRazorpayOrderId(String razorpayOrderId);

    boolean existsByDoctorIdAndPetIdAndAppointmentDate(Long doctorId, Long petId, LocalDate appointmentDate);
}
