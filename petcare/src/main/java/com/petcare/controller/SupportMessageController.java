package com.petcare.controller;

import com.petcare.dto.SendSupportMessageDTO;
import com.petcare.dto.SupportMessageDTO;
import com.petcare.entity.Role;
import com.petcare.service.SupportMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/support/messages")
public class SupportMessageController {

    @Autowired
    private SupportMessageService supportMessageService;

    @Autowired
    private com.petcare.repository.UserRepository userRepository;

    /**
     * Send a message in a support ticket
     * POST /api/support/messages
     */
    @PostMapping
    public ResponseEntity<?> sendMessage(@RequestBody SendSupportMessageDTO dto) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            
            com.petcare.entity.User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            SupportMessageDTO message = supportMessageService.sendMessage(user.getId(), dto);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Message sent successfully",
                    "data", message
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * Get chat history for a ticket
     * GET /api/support/messages/{ticketId}
     */
    @GetMapping("/{ticketId}")
    public ResponseEntity<?> getTicketMessages(@PathVariable Long ticketId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            
            com.petcare.entity.User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<SupportMessageDTO> messages = supportMessageService.getTicketMessages(ticketId, user.getId(), user.getRole());
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "messages", messages
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}
