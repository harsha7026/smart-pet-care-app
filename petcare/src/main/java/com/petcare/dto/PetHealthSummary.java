package com.petcare.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PetHealthSummary {
    public Long petId;
    public String petName;
    public String species;
    public String breed;
    public Integer age;
    public BigDecimal weight;
    public String medicalNotes;
    public List<MedicalHistoryItem> medicalHistory;
    public List<VaccinationItem> vaccinations;
    public List<HealthMetricItem> healthMetrics;
    public List<ReminderItem> reminders;

    public static class MedicalHistoryItem {
        public Long id;
        public LocalDate visitDate;
        public String diagnosis;
        public String treatment;
        public String prescription;
        public String notes;
    }

    public static class VaccinationItem {
        public Long id;
        public String vaccineName;
        public LocalDate administeredDate;
        public LocalDate dueDate;
        public String status;
        public String notes;
    }

    public static class HealthMetricItem {
        public Long id;
        public LocalDate recordDate;
        public Double weight;
        public Integer calorieIntake;
        public Integer activityLevel;
        public String stressLevel;
        public String bloodPressure;
        public String notes;
        public Boolean vetEntered;
    }

    public static class ReminderItem {
        public Long id;
        public String title;
        public String description;
        public String reminderType;
        public LocalDate reminderDate;
        public String status;
    }
}
