package com.jeff.hotel_management_system.dto;

import com.jeff.hotel_management_system.entity.Room;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private Long price;
    private LocalDate startDate;
    private LocalDate endDate;
    private String roomNr;
    private String clientEmail;
    private Room room;
}
