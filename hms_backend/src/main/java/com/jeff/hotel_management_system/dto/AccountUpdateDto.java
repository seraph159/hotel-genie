package com.jeff.hotel_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountUpdateDto {
    private String name;
    private String phone;
    private String paymentType; // e.g., CREDIT_CARD or BANK_ACCOUNT
}
