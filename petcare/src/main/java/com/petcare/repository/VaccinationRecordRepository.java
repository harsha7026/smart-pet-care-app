package com.petcare.repository;

import com.petcare.entity.Pet;
import com.petcare.entity.VaccinationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VaccinationRecordRepository extends JpaRepository<VaccinationRecord, Long> {
    List<VaccinationRecord> findByPetOrderByDueDateAsc(Pet pet);
    List<VaccinationRecord> findByPetAndDueDateBetweenOrderByDueDateAsc(Pet pet, LocalDate from, LocalDate to);
}
