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
@Table(name = "Occupies")
public class Occupies {

    // composite primary key
    @EmbeddedId
    private OccupiesId id;

    @ManyToOne
    @JoinColumn(name = "client_email", nullable = false, insertable = false, updatable = false)
    private Client client;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "booking_start_date", referencedColumnName = "start_date", insertable = false, updatable = false),
            @JoinColumn(name = "booking_room_nr", referencedColumnName = "room_nr", insertable = false, updatable = false)
    })
    private Booking booking;

    // Getters, Setters, equals, hashCode
}
