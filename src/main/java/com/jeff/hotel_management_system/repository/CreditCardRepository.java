package com.jeff.hotel_management_system.repository;

import com.jeff.hotel_management_system.entity.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditCardRepository extends JpaRepository<CreditCard, String> {
}