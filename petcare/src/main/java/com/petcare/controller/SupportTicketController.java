package com.petcare.controller;

import com.petcare.dto.CreateSupportTicketDTO;
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
@RequestMapping("/api/support/tickets")
public class SupportTicketController {

    @Autowired
    private SupportTicketService supportTicketService;

    @Autowired
    private com.petcare.repository.UserRepository userRepository;

    /**
     * Create a new support ticket
     * POST /api/support/tickets
     */
    @PostMapping
    public ResponseEntity<?> createTicket(@RequestBody CreateSupportTicketDTO dto) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            
            com.petcare.entity.User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            SupportTicketDTO ticket = supportTicketService.createTicket(user.getId(), dto);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Support ticket created successfully",
                    "ticket", ticket
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * Get tickets for logged-in user
     * GET /api/support/tickets/user
     */
    @GetMapping("/user")
    public ResponseEntity<?> getUserTickets() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            
            com.petcare.entity.User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<SupportTicketDTO> tickets = supportTicketService.getUserTickets(user.getId());
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
     * Get ticket details
     * GET /api/support/tickets/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getTicketDetails(@PathVariable Long id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            
            com.petcare.entity.User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            SupportTicketDTO ticket = supportTicketService.getTicketDetails(id, user.getId(), user.getRole());
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "ticket", ticket
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}
