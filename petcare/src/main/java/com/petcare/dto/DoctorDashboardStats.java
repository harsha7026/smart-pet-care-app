package com.petcare.dto;

public class DoctorDashboardStats {
    private Long totalAppointments;
    private Long pendingAppointmentsToday;
    private Long uniquePetsTreated;
    private Long appointmentsThisMonth;

    public DoctorDashboardStats() {}

    public DoctorDashboardStats(Long totalAppointments, Long pendingAppointmentsToday, Long uniquePetsTreated, Long appointmentsThisMonth) {
        this.totalAppointments = totalAppointments;
        this.pendingAppointmentsToday = pendingAppointmentsToday;
        this.uniquePetsTreated = uniquePetsTreated;
        this.appointmentsThisMonth = appointmentsThisMonth;
    }

    public Long getTotalAppointments() {
        return totalAppointments;
    }

    public void setTotalAppointments(Long totalAppointments) {
        this.totalAppointments = totalAppointments;
    }

    public Long getPendingAppointmentsToday() {
        return pendingAppointmentsToday;
    }

    public void setPendingAppointmentsToday(Long pendingAppointmentsToday) {
        this.pendingAppointmentsToday = pendingAppointmentsToday;
    }

    public Long getUniquePetsTreated() {
        return uniquePetsTreated;
    }

    public void setUniquePetsTreated(Long uniquePetsTreated) {
        this.uniquePetsTreated = uniquePetsTreated;
    }

    public Long getAppointmentsThisMonth() {
        return appointmentsThisMonth;
    }

    public void setAppointmentsThisMonth(Long appointmentsThisMonth) {
        this.appointmentsThisMonth = appointmentsThisMonth;
    }
}
