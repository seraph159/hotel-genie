package com.jeff.hotel_management_system.repository;

import com.jeff.hotel_management_system.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, String> {

    boolean existsByEmail(String email);

    Optional<Admin> findByEmail(String email);
}
