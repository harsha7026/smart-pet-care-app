package com.petcare.dto;

import java.math.BigDecimal;

public class DoctorInfo {
    public Long id;
    public String name;
    public String email;
    public String phone;
    public String specialization;
    public BigDecimal consultationFee;

    public DoctorInfo() {}

    public DoctorInfo(Long id, String name, String email, String phone, String specialization, BigDecimal consultationFee) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.specialization = specialization;
        this.consultationFee = consultationFee;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public BigDecimal getConsultationFee() { return consultationFee; }
    public void setConsultationFee(BigDecimal consultationFee) { this.consultationFee = consultationFee; }
}
