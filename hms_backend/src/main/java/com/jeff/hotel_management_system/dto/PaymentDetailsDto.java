package com.jeff.hotel_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDetailsDto {
    private String paymentType; // CREDIT_CARD or BANK_ACCOUNT

    // Credit Card-specific fields
    private String holdername;
    private String cardnumber;
    private String type; // e.g., VISA, MasterCard

    // Bank Account-specific fields
    private String bank;
    private String accountnumber;
    private String routingnumber;
}
