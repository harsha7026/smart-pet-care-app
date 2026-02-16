package com.petcare.controller;

import com.petcare.dto.CheckoutRequest;
import com.petcare.dto.OrderPaymentConfirmation;
import com.petcare.entity.Order;
import com.petcare.entity.User;
import com.petcare.model.OrderStatus;
import com.petcare.repository.UserRepository;
import com.petcare.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    public OrderController(OrderService orderService, UserRepository userRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    private Long resolveUserId(HttpSession session) {
        // Try to get from session first
        Object userId = session.getAttribute("userId");
        if (userId instanceof Long) {
            return (Long) userId;
        }
        
        // Fallback to authenticated principal
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            String email = auth.getName();
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                return userOpt.get().getId();
            }
        }
        
        return null;
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody CheckoutRequest request, HttpSession session) {
        try {
            Long userId = resolveUserId(session);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not authenticated"));
            }
            Map<String, Object> payload = orderService.createOrderWithRazorpay(userId, request);
            return ResponseEntity.ok(payload);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{orderId}/confirm-payment")
    public ResponseEntity<?> confirmPayment(@PathVariable Long orderId, @RequestBody OrderPaymentConfirmation confirmation) {
        try {
            System.out.println("Confirm payment request for orderId: " + orderId);
            System.out.println("Confirmation: " + confirmation.getRazorpayPaymentId() + ", " + confirmation.getRazorpayOrderId());
            Order order = orderService.confirmPayment(orderId, confirmation);
            return ResponseEntity.ok(Map.of(
                "orderId", order.getId(),
                "status", order.getStatus(),
                "paymentStatus", order.getPaymentStatus(),
                "razorpayPaymentId", order.getRazorpayPaymentId()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error confirming payment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/my")
    public ResponseEntity<?> myOrders(HttpSession session) {
        try {
            Long userId = resolveUserId(session);
            System.out.println("🛍️  Get orders for user: " + userId);
            if (userId == null) {
                System.err.println("❌ User not authenticated");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not authenticated"));
            }
            List<Order> orders = orderService.getOrdersForUser(userId);
            System.out.println("✅ Found " + orders.size() + " orders for user " + userId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            System.err.println("❌ Error fetching orders: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable Long orderId, HttpSession session) {
        try {
            Long userId = resolveUserId(session);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not authenticated"));
            }
            
            Order order = orderService.findById(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found"));
            
            // Verify order belongs to user (unless admin)
            if (!order.getUser().getId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Access denied"));
            }
            
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    // ========== ADMIN ENDPOINTS ==========
    
    @GetMapping("/admin/all")
    public ResponseEntity<?> getAllOrders() {
        try {
            List<Order> orders = orderService.getAllOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/admin/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId,
                                               @RequestBody Map<String, String> request) {
        try {
            String statusStr = request.get("status");
            if (statusStr == null || statusStr.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Status is required"));
            }
            
            OrderStatus status = OrderStatus.valueOf(statusStr);
            String expectedDeliveryDate = request.get("expectedDeliveryDate");
            LocalDate delivery = expectedDeliveryDate != null && !expectedDeliveryDate.isBlank()
                    ? LocalDate.parse(expectedDeliveryDate)
                    : null;
            Order updated = orderService.updateStatus(orderId, status, delivery);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid status: " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long orderId,
                                          @RequestParam OrderStatus status,
                                          @RequestParam(required = false) String expectedDeliveryDate) {
        try {
            LocalDate delivery = expectedDeliveryDate != null && !expectedDeliveryDate.isBlank()
                    ? LocalDate.parse(expectedDeliveryDate)
                    : null;
            Order updated = orderService.updateStatus(orderId, status, delivery);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
}
