package com.jeff.hotel_management_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Booking")
public class Booking {

    // composite primary key
    @EmbeddedId
    private BookingId id;

    @Column(nullable = false, scale = 2, precision = 10)
    private Long price;

    @ManyToOne
    @JoinColumn(name = "client_email", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "room_nr", insertable = false, updatable = false, nullable = false)
    private Room room;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    // Getters and Setters, equals, hashCode, toString
}
