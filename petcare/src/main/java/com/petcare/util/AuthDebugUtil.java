package com.petcare.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthDebugUtil {
    public static void logAuthStatus(String location) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("=== AUTH DEBUG [" + location + "] ===");
        System.out.println("Authentication: " + auth);
        if (auth != null) {
            System.out.println("  isAuthenticated: " + auth.isAuthenticated());
            System.out.println("  Name (Principal): " + auth.getName());
            System.out.println("  Type: " + auth.getClass().getSimpleName());
            System.out.println("  Authorities: " + auth.getAuthorities());
        } else {
            System.out.println("  AUTH IS NULL!");
        }
        System.out.println("===================================");
    }
}
