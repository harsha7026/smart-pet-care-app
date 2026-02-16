package com.petcare.service;

import com.petcare.dto.CartItemRequest;
import com.petcare.dto.CheckoutRequest;
import com.petcare.dto.OrderPaymentConfirmation;
import com.petcare.entity.Order;
import com.petcare.entity.OrderItem;
import com.petcare.entity.Product;
import com.petcare.entity.User;
import com.petcare.model.OrderStatus;
import com.petcare.model.PaymentStatus;
import com.petcare.repository.OrderItemRepository;
import com.petcare.repository.OrderRepository;
import com.petcare.repository.ProductRepository;
import com.petcare.repository.UserRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Value("${razorpay.key-id}")
    private String razorpayKeyId;

    @Value("${razorpay.key-secret}")
    private String razorpayKeySecret;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        ProductRepository productRepository,
                        UserRepository userRepository,
                        EmailService emailService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    private void validateStock(List<CartItemRequest> items) {
        for (CartItemRequest item : items) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + item.getProductId()));
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
            }
        }
    }

    @Transactional
    public Map<String, Object> createOrderWithRazorpay(Long userId, CheckoutRequest request) {
        validateStock(request.getItems());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        BigDecimal computedTotal = request.getItems().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (request.getTotalAmount() != null && request.getTotalAmount().compareTo(computedTotal) != 0) {
            throw new IllegalArgumentException("Total mismatch");
        }

        // Create local order first
        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(computedTotal);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setStatus(OrderStatus.PLACED);
        order.setShippingAddress(request.getShippingAddress());
        order.setExpectedDeliveryDate(LocalDate.now().plusDays(5));
        order = orderRepository.save(order);

        Order finalOrder = order;
        List<OrderItem> orderItems = request.getItems().stream().map(item -> {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + item.getProductId()));
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(finalOrder);
            orderItem.setProduct(product);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setUnitPrice(item.getUnitPrice());
            orderItem.setSubtotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            return orderItem;
        }).collect(Collectors.toList());
        orderItemRepository.saveAll(orderItems);
        finalOrder.setItems(orderItems);

        try {
            RazorpayClient client = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
            JSONObject options = new JSONObject();
            options.put("amount", computedTotal.multiply(BigDecimal.valueOf(100)).intValue());
            options.put("currency", "INR");
            options.put("receipt", "order_" + order.getId());
            com.razorpay.Order razorpayOrder = client.orders.create(options);

            order.setRazorpayOrderId(razorpayOrder.get("id"));
            orderRepository.save(order);

            Map<String, Object> payload = new HashMap<>();
            payload.put("orderId", order.getId());
            payload.put("razorpayOrderId", razorpayOrder.get("id"));
            payload.put("amount", computedTotal);
            payload.put("currency", "INR");
            payload.put("razorpayKeyId", razorpayKeyId);
            return payload;
        } catch (RazorpayException ex) {
            throw new IllegalStateException("Failed to create Razorpay order: " + ex.getMessage());
        }
    }

    @Transactional
    public Order confirmPayment(Long orderId, OrderPaymentConfirmation confirmation) {
        try {
            System.out.println("=== CONFIRM PAYMENT START ===");
            System.out.println("Order ID: " + orderId);
            System.out.println("Payment ID: " + confirmation.getRazorpayPaymentId());
            
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
            
            System.out.println("Order found: " + order.getId());
            System.out.println("Current status: " + order.getPaymentStatus());
            
            order.setPaymentStatus(PaymentStatus.SUCCESS);
            order.setStatus(OrderStatus.PLACED);
            order.setRazorpayPaymentId(confirmation.getRazorpayPaymentId());
            order.setRazorpayOrderId(confirmation.getRazorpayOrderId());
            order.setRazorpaySignature(confirmation.getRazorpaySignature());
            
            System.out.println("Saving order...");
            Order saved = orderRepository.save(order);
            System.out.println("Order saved: " + saved.getId());
            
            try {
                emailService.sendEmail(order.getUser().getEmail(), "Order Confirmation", 
                    "Your order #" + order.getId() + " has been placed. Total: ₹" + order.getTotalAmount() + 
                    "\nExpected Delivery: " + order.getExpectedDeliveryDate());
            } catch (Exception emailEx) {
                System.err.println("Email sending failed (non-critical): " + emailEx.getMessage());
            }
            
            System.out.println("=== CONFIRM PAYMENT SUCCESS ===");
            return saved;
        } catch (Exception e) {
            System.err.println("=== CONFIRM PAYMENT ERROR ===");
            e.printStackTrace();
            throw e;
        }
    }

    public List<Order> getOrdersForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return orderRepository.findByUser(user);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional
    public Order updateStatus(Long orderId, OrderStatus status, LocalDate expectedDeliveryDate) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        
        OrderStatus previousStatus = order.getStatus();
        order.setStatus(status);
        
        if (expectedDeliveryDate != null) {
            order.setExpectedDeliveryDate(expectedDeliveryDate);
        }
        
        Order savedOrder = orderRepository.save(order);
        
        // Send email notification on status change
        try {
            String userEmail = order.getUser().getEmail();
            String subject = "Order #" + orderId + " Status Update";
            String message = buildStatusUpdateMessage(order, status);
            emailService.sendEmail(userEmail, subject, message);
        } catch (Exception emailEx) {
            System.err.println("Email sending failed (non-critical): " + emailEx.getMessage());
        }
        
        return savedOrder;
    }

    private String buildStatusUpdateMessage(Order order, OrderStatus status) {
        StringBuilder message = new StringBuilder();
        message.append("Dear Customer,\n\n");
        message.append("Your order #").append(order.getId()).append(" status has been updated.\n\n");
        message.append("New Status: ").append(formatStatus(status)).append("\n");
        message.append("Order Total: ₹").append(order.getTotalAmount()).append("\n");
        
        if (order.getExpectedDeliveryDate() != null) {
            message.append("Expected Delivery: ").append(order.getExpectedDeliveryDate()).append("\n");
        }
        
        message.append("\n");
        
        switch (status) {
            case PAYMENT_CONFIRMED:
                message.append("Your payment has been confirmed. We are preparing your order.\n");
                break;
            case PACKED:
                message.append("Your order has been packed and is ready for shipment.\n");
                break;
            case SHIPPED:
                message.append("Your order has been shipped and is on its way!\n");
                break;
            case OUT_FOR_DELIVERY:
                message.append("Your order is out for delivery. You should receive it soon!\n");
                break;
            case DELIVERED:
                message.append("Your order has been delivered. Thank you for shopping with us!\n");
                break;
            case CANCELLED:
                message.append("Your order has been cancelled.\n");
                break;
            default:
                break;
        }
        
        message.append("\nThank you,\nPetCare Team");
        return message.toString();
    }

    private String formatStatus(OrderStatus status) {
        return status.name().replace("_", " ");
    }

    public Optional<Order> findById(Long orderId) {
        return orderRepository.findById(orderId);
    }
}
