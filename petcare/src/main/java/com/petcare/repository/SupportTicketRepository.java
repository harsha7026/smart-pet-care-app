package com.petcare.repository;

import com.petcare.entity.SupportTicket;
import com.petcare.entity.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
    List<SupportTicket> findByUserId(Long userId);
    List<SupportTicket> findByStatus(TicketStatus status);
    List<SupportTicket> findAllByOrderByCreatedAtDesc();
    Optional<SupportTicket> findByIdAndUserId(Long id, Long userId);
}
