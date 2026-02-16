package com.petcare.service;

import com.petcare.dto.CreateSupportTicketDTO;
import com.petcare.dto.SupportTicketDTO;
import com.petcare.dto.SupportTicketEmailDTO;
import com.petcare.entity.*;
import com.petcare.repository.SupportTicketRepository;
import com.petcare.repository.SupportMessageRepository;
import com.petcare.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SupportTicketService {

    private static final Logger log = LoggerFactory.getLogger(SupportTicketService.class);

    @Autowired
    private SupportTicketRepository ticketRepository;

    @Autowired
    private SupportMessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    /**
     * Create a new support ticket
     */
    @Transactional
    public SupportTicketDTO createTicket(Long userId, CreateSupportTicketDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        SupportTicket ticket = new SupportTicket();
        ticket.setUser(user);
        ticket.setUserRole(user.getRole());
        ticket.setCategory(SupportCategory.valueOf(dto.getCategory().toUpperCase()));
        ticket.setSubject(dto.getSubject());
        ticket.setDescription(dto.getDescription());
        ticket.setStatus(TicketStatus.OPEN);

        SupportTicket savedTicket = ticketRepository.save(ticket);

        // Send email notification to admin and user
        sendTicketCreatedEmail(savedTicket);

        log.info("Support ticket created: ID={}, User={}, Category={}", 
                savedTicket.getId(), user.getEmail(), dto.getCategory());

        return convertToDTO(savedTicket, 0);
    }

    /**
     * Get tickets for logged-in user
     */
    public List<SupportTicketDTO> getUserTickets(Long userId) {
        List<SupportTicket> tickets = ticketRepository.findByUserId(userId);
        return tickets.stream()
                .map(ticket -> {
                    int messageCount = messageRepository.findByTicketIdOrderByTimestampAsc(ticket.getId()).size();
                    return convertToDTO(ticket, messageCount);
                })
                .collect(Collectors.toList());
    }

    /**
     * Get all tickets (Admin only)
     */
    public List<SupportTicketDTO> getAllTickets() {
        List<SupportTicket> tickets = ticketRepository.findAllByOrderByCreatedAtDesc();
        return tickets.stream()
                .map(ticket -> {
                    int messageCount = messageRepository.findByTicketIdOrderByTimestampAsc(ticket.getId()).size();
                    return convertToDTO(ticket, messageCount);
                })
                .collect(Collectors.toList());
    }

    /**
     * Get tickets by status (Admin only)
     */
    public List<SupportTicketDTO> getTicketsByStatus(TicketStatus status) {
        List<SupportTicket> tickets = ticketRepository.findByStatus(status);
        return tickets.stream()
                .map(ticket -> {
                    int messageCount = messageRepository.findByTicketIdOrderByTimestampAsc(ticket.getId()).size();
                    return convertToDTO(ticket, messageCount);
                })
                .collect(Collectors.toList());
    }

    /**
     * Get ticket details (check access)
     */
    public SupportTicketDTO getTicketDetails(Long ticketId, Long userId, Role userRole) {
        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        // Check access: user can only view their own tickets, admin can view all
        if (!userRole.equals(Role.ADMIN) && !ticket.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        int messageCount = messageRepository.findByTicketIdOrderByTimestampAsc(ticketId).size();
        return convertToDTO(ticket, messageCount);
    }

    /**
     * Update ticket status (Admin only)
     */
    @Transactional
    public SupportTicketDTO updateTicketStatus(Long ticketId, String newStatus) {
        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        TicketStatus oldStatus = ticket.getStatus();
        TicketStatus status = TicketStatus.valueOf(newStatus.toUpperCase());
        ticket.setStatus(status);

        SupportTicket updatedTicket = ticketRepository.save(ticket);

        // Send email notification
        if (status.equals(TicketStatus.RESOLVED) || status.equals(TicketStatus.CLOSED)) {
            sendTicketStatusUpdateEmail(updatedTicket, status);
        }

        log.info("Ticket status updated: ID={}, Status: {} -> {}", 
                ticketId, oldStatus, status);

        int messageCount = messageRepository.findByTicketIdOrderByTimestampAsc(ticketId).size();
        return convertToDTO(updatedTicket, messageCount);
    }

    /**
     * Convert entity to DTO
     */
    private SupportTicketDTO convertToDTO(SupportTicket ticket, int messageCount) {
        return new SupportTicketDTO(
                ticket.getId(),
                ticket.getUser().getId(),
                ticket.getUser().getName(),
                ticket.getUser().getEmail(),
                ticket.getUserRole().toString(),
                ticket.getCategory().getDisplayName(),
                ticket.getSubject(),
                ticket.getDescription(),
                ticket.getStatus().getDisplayName(),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt(),
                messageCount
        );
    }

    /**
     * Send email when ticket is created
     */
    private void sendTicketCreatedEmail(SupportTicket ticket) {
        try {
            SupportTicketEmailDTO emailData = new SupportTicketEmailDTO();
            emailData.setRecipientEmail(ticket.getUser().getEmail());
            emailData.setUserName(ticket.getUser().getName());
            emailData.setTicketId(ticket.getId());
            emailData.setCategory(ticket.getCategory().getDisplayName());
            emailData.setSubject(ticket.getSubject());
            emailData.setTicketUrl("http://localhost:3000/support/tickets/" + ticket.getId());

            emailService.sendSupportTicketCreatedEmail(emailData);
        } catch (Exception e) {
            log.warn("Failed to send ticket created email: {}", e.getMessage());
        }
    }

    /**
     * Send email when ticket status is updated
     */
    private void sendTicketStatusUpdateEmail(SupportTicket ticket, TicketStatus status) {
        try {
            SupportTicketEmailDTO emailData = new SupportTicketEmailDTO();
            emailData.setRecipientEmail(ticket.getUser().getEmail());
            emailData.setUserName(ticket.getUser().getName());
            emailData.setTicketId(ticket.getId());
            emailData.setStatus(status.getDisplayName());
            emailData.setTicketUrl("http://localhost:3000/support/tickets/" + ticket.getId());

            emailService.sendSupportTicketStatusUpdateEmail(emailData);
        } catch (Exception e) {
            log.warn("Failed to send ticket status update email: {}", e.getMessage());
        }
    }
}
