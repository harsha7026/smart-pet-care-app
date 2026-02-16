package com.petcare.repository;

import com.petcare.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    
    List<Pet> findByUserId(Long userId);
    
    List<Pet> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    Optional<Pet> findByIdAndUserId(Long id, Long userId);
}
