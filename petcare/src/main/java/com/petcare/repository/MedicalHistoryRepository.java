package com.petcare.repository;

import com.petcare.entity.MedicalHistory;
import com.petcare.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MedicalHistoryRepository extends JpaRepository<MedicalHistory, Long> {
    List<MedicalHistory> findByPetOrderByVisitDateDesc(Pet pet);
    List<MedicalHistory> findByPetAndVisitDateBetweenOrderByVisitDateDesc(Pet pet, LocalDate from, LocalDate to);
}
