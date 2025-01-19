package com.jeff.hotel_management_system.repository;

import com.jeff.hotel_management_system.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, String> {

    Optional<Client> findByEmail(String email);
    boolean existsByEmail(String email);

}