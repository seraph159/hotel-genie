package com.jeff.hotel_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponseDto {
    private String name;
    private String email;
    private String phone;
    private String paymentType;

    // Credit Card fields
    private String holdername;
    private String cardnumber;
    private String cardType;

    // Bank Account fields
    private String bank;
    private String accountnumber;
    private String routingnumber;
}
