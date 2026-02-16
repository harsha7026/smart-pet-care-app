package com.petcare.dto;

public class OwnerDashboardStats {
    private Long totalPets;
    private Long upcomingAppointments;
    private Long pendingPrescriptions;
    private Long healthRecords;

    public OwnerDashboardStats() {}

    public OwnerDashboardStats(Long totalPets, Long upcomingAppointments, Long pendingPrescriptions, Long healthRecords) {
        this.totalPets = totalPets;
        this.upcomingAppointments = upcomingAppointments;
        this.pendingPrescriptions = pendingPrescriptions;
        this.healthRecords = healthRecords;
    }

    public Long getTotalPets() {
        return totalPets;
    }

    public void setTotalPets(Long totalPets) {
        this.totalPets = totalPets;
    }

    public Long getUpcomingAppointments() {
        return upcomingAppointments;
    }

    public void setUpcomingAppointments(Long upcomingAppointments) {
        this.upcomingAppointments = upcomingAppointments;
    }

    public Long getPendingPrescriptions() {
        return pendingPrescriptions;
    }

    public void setPendingPrescriptions(Long pendingPrescriptions) {
        this.pendingPrescriptions = pendingPrescriptions;
    }

    public Long getHealthRecords() {
        return healthRecords;
    }

    public void setHealthRecords(Long healthRecords) {
        this.healthRecords = healthRecords;
    }
}
