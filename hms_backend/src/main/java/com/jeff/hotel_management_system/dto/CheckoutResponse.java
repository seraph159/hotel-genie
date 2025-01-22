package com.jeff.hotel_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutResponse {

    private String status;
    private String message;
    private String checkoutId;
    private String checkoutLink;
}
