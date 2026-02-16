package com.petcare.repository;

import com.petcare.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByIsActive(Boolean isActive);

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
            "AND (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Product> findByCategoryAndSearch(@Param("categoryId") Long categoryId,
                                          @Param("search") String search);
    
    @Query("SELECT p FROM Product p WHERE (:categoryId IS NULL OR p.category.id = :categoryId) " +
            "AND (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Product> findAllByCategoryAndSearch(@Param("categoryId") Long categoryId,
                                             @Param("search") String search);
}
