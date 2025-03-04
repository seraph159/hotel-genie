package com.jeff.hotel_management_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Room")
public class Room {

    @Id
    @Column(name = "room_nr", length = 10)
    private String roomNr;

    @Column(nullable = false)
    private int floor;

    @Column(name = "max_occ", nullable = false)
    private int maxOccupancy;

    @Column(nullable = false)
    private boolean isAvailable;

    @Column(nullable = false)
    private Long basePrice; // Base price for the room

    // fields for personalized recommendations
    @Column(nullable = false, length = 50)
    private String roomType; // e.g., "Single", "Double", "Suite"

    @Column(nullable = false)
    private boolean hasSeaView;

    @Column(nullable = false)
    private boolean hasBalcony;

    @Column(nullable = false)
    private boolean hasWifi;

    @Column(nullable = false)
    private boolean hasAirConditioning;

    @Column(nullable = false)
    private boolean isPetFriendly;

    @Column(nullable = false, length = 255)
    private String amenities; // e.g., "Pool Access, Gym Access, Free Breakfast"

    @Column(nullable = true)
    private double rating; // Customer rating (1.0 to 5.0)

    @Column(nullable = true)
    private String preferredFor; // e.g., "Family, Honeymoon, Business"

}
