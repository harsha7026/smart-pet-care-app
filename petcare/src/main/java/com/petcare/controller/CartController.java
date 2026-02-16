package com.petcare.controller;

import com.petcare.entity.Cart;
import com.petcare.entity.User;
import com.petcare.repository.UserRepository;
import com.petcare.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    public CartController(CartService cartService, UserRepository userRepository) {
        this.cartService = cartService;
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

    @GetMapping
    public ResponseEntity<?> getCart(HttpSession session) {
        try {
            Long userId = resolveUserId(session);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not authenticated"));
            }
            
            Cart cart = cartService.getOrCreateCart(userId);
            BigDecimal total = cartService.getCartTotal(cart);
            
            return ResponseEntity.ok(Map.of(
                    "cart", cart,
                    "total", total,
                    "itemCount", cart.getItems().size()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody Map<String, Object> request, HttpSession session) {
        try {
            Long userId = resolveUserId(session);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not authenticated"));
            }

            Long productId = Long.valueOf(request.get("productId").toString());
            Integer quantity = Integer.valueOf(request.get("quantity").toString());

            Cart cart = cartService.addToCart(userId, productId, quantity);
            BigDecimal total = cartService.getCartTotal(cart);

            return ResponseEntity.ok(Map.of(
                    "message", "Product added to cart",
                    "cart", cart,
                    "total", total
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateCartItem(@RequestBody Map<String, Object> request, HttpSession session) {
        try {
            Long userId = resolveUserId(session);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not authenticated"));
            }

            Long cartItemId = Long.valueOf(request.get("cartItemId").toString());
            Integer quantity = Integer.valueOf(request.get("quantity").toString());

            Cart cart = cartService.updateCartItem(userId, cartItemId, quantity);
            BigDecimal total = cartService.getCartTotal(cart);

            return ResponseEntity.ok(Map.of(
                    "message", "Cart updated",
                    "cart", cart,
                    "total", total
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<?> removeFromCart(@PathVariable Long cartItemId, HttpSession session) {
        try {
            Long userId = resolveUserId(session);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not authenticated"));
            }

            Cart cart = cartService.removeFromCart(userId, cartItemId);
            BigDecimal total = cartService.getCartTotal(cart);

            return ResponseEntity.ok(Map.of(
                    "message", "Item removed from cart",
                    "cart", cart,
                    "total", total
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart(HttpSession session) {
        try {
            Long userId = resolveUserId(session);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not authenticated"));
            }

            cartService.clearCart(userId);
            return ResponseEntity.ok(Map.of("message", "Cart cleared successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
