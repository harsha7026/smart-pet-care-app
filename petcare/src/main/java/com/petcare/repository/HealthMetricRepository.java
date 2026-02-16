package com.petcare.repository;

import com.petcare.entity.HealthMetric;
import com.petcare.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HealthMetricRepository extends JpaRepository<HealthMetric, Long> {
    List<HealthMetric> findByPetOrderByRecordDateDesc(Pet pet);
    List<HealthMetric> findByPetAndRecordDateBetweenOrderByRecordDateAsc(Pet pet, LocalDate from, LocalDate to);
}
