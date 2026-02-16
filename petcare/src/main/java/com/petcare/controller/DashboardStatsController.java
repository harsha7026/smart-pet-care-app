package com.petcare.controller;

import com.petcare.dto.AdminDashboardStats;
import com.petcare.dto.DoctorDashboardStats;
import com.petcare.dto.OwnerDashboardStats;
import com.petcare.entity.User;
import com.petcare.service.DashboardStatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
public class DashboardStatsController {

    private final DashboardStatsService dashboardStatsService;

    public DashboardStatsController(DashboardStatsService dashboardStatsService) {
        this.dashboardStatsService = dashboardStatsService;
    }

    /**
     * GET /api/stats/owner - Get pet owner dashboard statistics
     */
    @GetMapping("/owner")
    public ResponseEntity<OwnerDashboardStats> getOwnerStats(@AuthenticationPrincipal User user) {
        OwnerDashboardStats stats = dashboardStatsService.getOwnerStats(user.getId());
        return ResponseEntity.ok(stats);
    }

    /**
     * GET /api/stats/doctor - Get doctor dashboard statistics
     */
    @GetMapping("/doctor")
    public ResponseEntity<DoctorDashboardStats> getDoctorStats(@AuthenticationPrincipal User user) {
        DoctorDashboardStats stats = dashboardStatsService.getDoctorStats(user.getId());
        return ResponseEntity.ok(stats);
    }

    /**
     * GET /api/stats/admin - Get admin dashboard statistics
     */
    @GetMapping("/admin")
    public ResponseEntity<AdminDashboardStats> getAdminStats() {
        AdminDashboardStats stats = dashboardStatsService.getAdminStats();
        return ResponseEntity.ok(stats);
    }
}
