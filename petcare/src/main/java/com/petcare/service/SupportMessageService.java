package com.petcare.service;

import com.petcare.dto.SendSupportMessageDTO;
import com.petcare.dto.SupportMessageDTO;
import com.petcare.dto.SupportMessageEmailDTO;
import com.petcare.entity.*;
import com.petcare.repository.SupportMessageRepository;
import com.petcare.repository.SupportTicketRepository;
import com.petcare.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SupportMessageService {

    private static final Logger log = LoggerFactory.getLogger(SupportMessageService.class);

    @Autowired
    private SupportMessageRepository messageRepository;

    @Autowired
    private SupportTicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    /**
     * Send a message in a support ticket
     */
    @Transactional
    public SupportMessageDTO sendMessage(Long userId, SendSupportMessageDTO dto) {
        User sender = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        SupportTicket ticket = ticketRepository.findById(dto.getTicketId())
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        // Check access: user can only message their own tickets, admin can message all
        if (!sender.getRole().equals(Role.ADMIN) && !ticket.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        SupportMessage message = new SupportMessage();
        message.setTicket(ticket);
        message.setSender(sender);
        message.setSenderRole(sender.getRole());
        message.setMessage(dto.getMessage());

        SupportMessage savedMessage = messageRepository.save(message);

        // Update ticket status to IN_PROGRESS if admin replies
        if (sender.getRole().equals(Role.ADMIN) && ticket.getStatus().equals(TicketStatus.OPEN)) {
            ticket.setStatus(TicketStatus.IN_PROGRESS);
            ticketRepository.save(ticket);
        }

        // Send email notification
        sendMessageNotificationEmail(savedMessage, ticket);

        log.info("Support message sent: TicketID={}, From={}", dto.getTicketId(), sender.getEmail());

        return convertToDTO(savedMessage);
    }

    /**
     * Get chat history for a ticket
     */
    public List<SupportMessageDTO> getTicketMessages(Long ticketId, Long userId, Role userRole) {
        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        // Check access
        if (!userRole.equals(Role.ADMIN) && !ticket.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        List<SupportMessage> messages = messageRepository.findByTicketIdOrderByTimestampAsc(ticketId);
        return messages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert entity to DTO
     */
    private SupportMessageDTO convertToDTO(SupportMessage message) {
        return new SupportMessageDTO(
                message.getId(),
                message.getTicket().getId(),
                message.getSender().getId(),
                message.getSender().getName(),
                message.getSenderRole().toString(),
                message.getMessage(),
                message.getTimestamp(),
                message.getSenderRole().equals(Role.ADMIN)
        );
    }

    /**
     * Send email notification for new message
     */
    private void sendMessageNotificationEmail(SupportMessage message, SupportTicket ticket) {
        try {
            // If admin sent message, notify the user
            if (message.getSenderRole().equals(Role.ADMIN)) {
                SupportMessageEmailDTO emailData = new SupportMessageEmailDTO();
                emailData.setRecipientEmail(ticket.getUser().getEmail());
                emailData.setUserName(ticket.getUser().getName());
                emailData.setAdminName(message.getSender().getName());
                emailData.setTicketId(ticket.getId());
                emailData.setSubject(ticket.getSubject());
                emailData.setTicketUrl("http://localhost:3000/support/tickets/" + ticket.getId());

                emailService.sendSupportMessageReplyEmail(emailData);
            }
        } catch (Exception e) {
            log.warn("Failed to send message notification email: {}", e.getMessage());
        }
    }
}
