package com.petcare.repository;

import com.petcare.entity.HealthReminder;
import com.petcare.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HealthReminderRepository extends JpaRepository<HealthReminder, Long> {
    List<HealthReminder> findByPetOrderByReminderDateAsc(Pet pet);
    List<HealthReminder> findByPetAndReminderDateBetweenOrderByReminderDateAsc(Pet pet, LocalDate from, LocalDate to);
}
