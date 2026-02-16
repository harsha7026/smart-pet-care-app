package com.petcare.repository;

import com.petcare.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
    Optional<UserAddress> findFirstByUserIdAndIsDefaultTrue(Long userId);
    List<UserAddress> findByUserId(Long userId);
}
