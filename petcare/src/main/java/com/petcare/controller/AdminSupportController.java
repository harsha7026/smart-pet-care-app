package com.petcare.controller;

import com.petcare.dto.SupportTicketDTO;
import com.petcare.entity.Role;
import com.petcare.entity.TicketStatus;
import com.petcare.service.SupportTicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/support")
public class AdminSupportController {

    @Autowired
    private SupportTicketService supportTicketService;

    @Autowired
    private com.petcare.repository.UserRepository userRepository;

    /**
     * Get all support tickets (Admin only)
     * GET /api/admin/support/tickets
     */
    @GetMapping("/tickets")
    public ResponseEntity<?> getAllTickets() {
        try {
            // Check if user is admin
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            
            com.petcare.entity.User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!user.getRole().equals(Role.ADMIN)) {
                return ResponseEntity.status(403).body(Map.of(
                        "success", false,
                        "message", "Access denied"
                ));
            }

            List<SupportTicketDTO> tickets = supportTicketService.getAllTickets();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "tickets", tickets
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * Get tickets by status (Admin only)
     * GET /api/admin/support/tickets/status/{status}
     */
    @GetMapping("/tickets/status/{status}")
    public ResponseEntity<?> getTicketsByStatus(@PathVariable String status) {
        try {
            // Check if user is admin
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            
            com.petcare.entity.User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!user.getRole().equals(Role.ADMIN)) {
                return ResponseEntity.status(403).body(Map.of(
                        "success", false,
                        "message", "Access denied"
                ));
            }

            TicketStatus ticketStatus = TicketStatus.valueOf(status.toUpperCase());
            List<SupportTicketDTO> tickets = supportTicketService.getTicketsByStatus(ticketStatus);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "tickets", tickets
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * Update ticket status (Admin only)
     * PUT /api/admin/support/tickets/{id}/status
     */
    @PutMapping("/tickets/{id}/status")
    public ResponseEntity<?> updateTicketStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            // Check if user is admin
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            
            com.petcare.entity.User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!user.getRole().equals(Role.ADMIN)) {
                return ResponseEntity.status(403).body(Map.of(
                        "success", false,
                        "message", "Access denied"
                ));
            }

            String newStatus = request.get("status");
            SupportTicketDTO updatedTicket = supportTicketService.updateTicketStatus(id, newStatus);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Ticket status updated successfully",
                    "ticket", updatedTicket
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}
