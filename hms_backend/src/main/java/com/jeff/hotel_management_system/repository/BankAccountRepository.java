package com.jeff.hotel_management_system.repository;

import com.jeff.hotel_management_system.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount, String> {
}
