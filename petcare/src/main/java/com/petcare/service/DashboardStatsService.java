package com.petcare.service;

import com.petcare.dto.AdminDashboardStats;
import com.petcare.dto.DoctorDashboardStats;
import com.petcare.dto.OwnerDashboardStats;
import com.petcare.entity.Role;
import com.petcare.entity.Status;
import com.petcare.model.AppointmentStatus;
import com.petcare.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Service
public class DashboardStatsService {

    private final PetRepository petRepository;
    private final AppointmentRepository appointmentRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final MedicalHistoryRepository medicalHistoryRepository;
    private final UserRepository userRepository;

    public DashboardStatsService(PetRepository petRepository, AppointmentRepository appointmentRepository,
                                PrescriptionRepository prescriptionRepository, MedicalHistoryRepository medicalHistoryRepository,
                                UserRepository userRepository) {
        this.petRepository = petRepository;
        this.appointmentRepository = appointmentRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.medicalHistoryRepository = medicalHistoryRepository;
        this.userRepository = userRepository;
    }

    /**
     * Get dashboard statistics for pet owner
     */
    public OwnerDashboardStats getOwnerStats(Long userId) {
        // Count total pets for this owner
        Long totalPets = (long) petRepository.findByUserId(userId).size();

        // Count upcoming appointments (next 7 days)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekFromNow = now.plusDays(7);
        Long upcomingAppointments = appointmentRepository.findByPetOwnerIdAndStatusOrderByAppointmentDateDesc(userId, AppointmentStatus.APPROVED)
                .stream()
                .filter(apt -> {
                    LocalDateTime aptDateTime = apt.getAppointmentDate().atTime(
                            apt.getAppointmentTime() != null ? apt.getAppointmentTime() : java.time.LocalTime.MIN
                    );
                    return aptDateTime.isAfter(now) && aptDateTime.isBefore(weekFromNow);
                })
                .count();

        // Count pending prescriptions (prescriptions for user's pets that exist)
        Long pendingPrescriptions = petRepository.findByUserId(userId)
                .stream()
                .flatMap(pet -> appointmentRepository.findByPetId(pet.getId()).stream())
                .filter(apt -> prescriptionRepository.existsByAppointmentId(apt.getId()))
                .count();

        // Count health records (medical history entries)
        Long healthRecords = petRepository.findByUserId(userId)
                .stream()
                .mapToLong(pet -> medicalHistoryRepository.findByPetOrderByVisitDateDesc(pet).size())
                .sum();

        return new OwnerDashboardStats(
                totalPets,
                upcomingAppointments,
                pendingPrescriptions,
                healthRecords
        );
    }

    /**
     * Get dashboard statistics for doctor
     */
    public DoctorDashboardStats getDoctorStats(Long doctorId) {
        // Count total appointments handled by this doctor
        Long totalAppointments = (long) appointmentRepository.findByDoctorIdOrderByAppointmentDateDesc(doctorId).size();

        // Count pending appointments today
        LocalDate today = LocalDate.now();
        Long pendingAppointmentsToday = appointmentRepository.findByDoctorIdAndStatusOrderByAppointmentDateDesc(doctorId, AppointmentStatus.PENDING)
                .stream()
                .filter(apt -> apt.getAppointmentDate().equals(today))
                .count();

        // Count unique pets treated (distinct pet IDs from appointments)
        Long uniquePetsTreated = appointmentRepository.findByDoctorIdOrderByAppointmentDateDesc(doctorId)
                .stream()
                .map(apt -> apt.getPet().getId())
                .distinct()
                .count();

        // Count appointments this month
        YearMonth currentMonth = YearMonth.now();
        LocalDate startOfMonth = currentMonth.atDay(1);
        LocalDate endOfMonth = currentMonth.atEndOfMonth();
        Long appointmentsThisMonth = appointmentRepository.findByDoctorIdOrderByAppointmentDateDesc(doctorId)
                .stream()
                .filter(apt -> {
                    LocalDate aptDate = apt.getAppointmentDate();
                    return !aptDate.isBefore(startOfMonth) && !aptDate.isAfter(endOfMonth);
                })
                .count();

        return new DoctorDashboardStats(
                totalAppointments,
                pendingAppointmentsToday,
                uniquePetsTreated,
                appointmentsThisMonth
        );
    }

    /**
     * Get dashboard statistics for admin
     */
    public AdminDashboardStats getAdminStats() {
        // Count total owners (role = PET_OWNER)
        Long totalOwners = (long) userRepository.findByRole(Role.PET_OWNER).size();

        // Count total doctors (role = VETERINARY_DOCTOR)
        Long totalDoctors = (long) userRepository.findByRole(Role.VETERINARY_DOCTOR).size();

        // Count total pets in system
        Long totalPets = petRepository.count();

        // Count appointments this month
        YearMonth currentMonth = YearMonth.now();
        LocalDate startOfMonth = currentMonth.atDay(1);
        LocalDate endOfMonth = currentMonth.atEndOfMonth();
        Long totalAppointmentsThisMonth = appointmentRepository.findAll()
                .stream()
                .filter(apt -> {
                    LocalDate aptDate = apt.getAppointmentDate();
                    return !aptDate.isBefore(startOfMonth) && !aptDate.isAfter(endOfMonth);
                })
                .count();

        // Count pending doctor approvals (doctors with status INACTIVE - awaiting approval)
        Long pendingDoctorApprovals = userRepository.findByRole(Role.VETERINARY_DOCTOR)
                .stream()
                .filter(user -> user.getStatus() == Status.INACTIVE)
                .count();

        return new AdminDashboardStats(
                totalOwners,
                totalDoctors,
                totalPets,
                totalAppointmentsThisMonth,
                pendingDoctorApprovals
        );
    }
}
