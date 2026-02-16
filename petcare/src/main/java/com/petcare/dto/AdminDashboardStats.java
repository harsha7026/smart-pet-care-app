package com.petcare.dto;

public class AdminDashboardStats {
    private Long totalOwners;
    private Long totalDoctors;
    private Long totalPets;
    private Long totalAppointmentsThisMonth;
    private Long pendingDoctorApprovals;

    public AdminDashboardStats() {}

    public AdminDashboardStats(Long totalOwners, Long totalDoctors, Long totalPets, Long totalAppointmentsThisMonth, Long pendingDoctorApprovals) {
        this.totalOwners = totalOwners;
        this.totalDoctors = totalDoctors;
        this.totalPets = totalPets;
        this.totalAppointmentsThisMonth = totalAppointmentsThisMonth;
        this.pendingDoctorApprovals = pendingDoctorApprovals;
    }

    public Long getTotalOwners() {
        return totalOwners;
    }

    public void setTotalOwners(Long totalOwners) {
        this.totalOwners = totalOwners;
    }

    public Long getTotalDoctors() {
        return totalDoctors;
    }

    public void setTotalDoctors(Long totalDoctors) {
        this.totalDoctors = totalDoctors;
    }

    public Long getTotalPets() {
        return totalPets;
    }

    public void setTotalPets(Long totalPets) {
        this.totalPets = totalPets;
    }

    public Long getTotalAppointmentsThisMonth() {
        return totalAppointmentsThisMonth;
    }

    public void setTotalAppointmentsThisMonth(Long totalAppointmentsThisMonth) {
        this.totalAppointmentsThisMonth = totalAppointmentsThisMonth;
    }

    public Long getPendingDoctorApprovals() {
        return pendingDoctorApprovals;
    }

    public void setPendingDoctorApprovals(Long pendingDoctorApprovals) {
        this.pendingDoctorApprovals = pendingDoctorApprovals;
    }
}
