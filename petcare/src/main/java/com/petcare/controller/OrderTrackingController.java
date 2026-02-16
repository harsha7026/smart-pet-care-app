package com.petcare.controller;

import com.petcare.dto.OrderTrackingEventDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderTrackingController {

    @Autowired
    private com.petcare.service.OrderTrackingService orderTrackingService;

    // Get tracking events for an order
    @GetMapping("/{orderId}/tracking")
    public ResponseEntity<?> getTracking(@PathVariable Long orderId) {
        try {
            List<OrderTrackingEventDTO> events = orderTrackingService.getTrackingEvents(orderId);
            return ResponseEntity.ok(Map.of("success", true, "events", events));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // Admin: add status update
    @PostMapping("/{orderId}/tracking")
    public ResponseEntity<?> addTrackingEvent(@PathVariable Long orderId, @RequestBody OrderTrackingEventDTO dto) {
        try {
            OrderTrackingEventDTO created = orderTrackingService.addTrackingEvent(orderId, dto);
            return ResponseEntity.ok(Map.of("success", true, "event", created));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
