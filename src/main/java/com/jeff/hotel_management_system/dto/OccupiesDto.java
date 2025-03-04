package com.jeff.hotel_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OccupiesDto {

    private String clientEmail; // Client's email (part of the composite key)
    private LocalDate bookingStartDate; // Booking start date (part of the composite key)
    private String bookingRoomNr; // Room number (part of the composite key)

}