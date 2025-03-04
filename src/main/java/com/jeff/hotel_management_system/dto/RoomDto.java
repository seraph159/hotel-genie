package com.jeff.hotel_management_system.dto;

import com.jeff.hotel_management_system.entity.Room;
import lombok.Data;

@Data
public class RoomDto {
    private Room room;
    private Double price;

    // Getters and setters
}
