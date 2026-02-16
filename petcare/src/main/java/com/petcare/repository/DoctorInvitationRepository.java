package com.petcare.repository;

import com.petcare.entity.DoctorInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorInvitationRepository extends JpaRepository<DoctorInvitation, Long> {
    Optional<DoctorInvitation> findByInvitationToken(String token);
    Optional<DoctorInvitation> findByEmail(String email);
    boolean existsByEmail(String email);
}
