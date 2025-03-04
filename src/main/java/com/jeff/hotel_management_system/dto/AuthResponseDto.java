package com.jeff.hotel_management_system.dto;

import lombok.Data;

@Data
public class AuthResponseDto {

    private String accessToken;
    private String role;

}

