package com.jeff.hotel_management_system.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidationResponseDto {
        private boolean valid;
        private String username;

}
