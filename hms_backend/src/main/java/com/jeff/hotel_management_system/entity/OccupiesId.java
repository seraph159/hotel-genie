package com.jeff.hotel_management_system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class OccupiesId implements Serializable {

    @Column(name = "client_email") // Ensure consistent naming
    private String clientEmail;

    @Column(name = "booking_start_date")
    private LocalDate bookingStartDate;

    @Column(name = "booking_room_nr")
    private String bookingRoomNr;

    // Getters, Setters, equals, hashCode
}
