package com.jeff.hotel_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponseDto {

    private String roomNr;
    private int floor;
    private int maxOccupancy;
    private boolean isAvailable;
    private Long basePrice;
    private String roomType;
    private boolean hasSeaView;
    private boolean hasBalcony;
    private boolean hasWifi;
    private boolean hasAirConditioning;
    private boolean isPetFriendly;
    private String amenities;
    private Double rating;
    private String preferredFor;
    private String description;
}