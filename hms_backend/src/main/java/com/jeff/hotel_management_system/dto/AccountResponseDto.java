package com.jeff.hotel_management_system.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponseDto {
    private String name;
    private String email;
    private String phone;
    private String paymentType;

    // Credit card details
    private String holdername;
    private String cardnumber;
    private String cardType;

    // Bank account details
    private String bank;
    private String accountnumber;
    private String routingnumber;
}