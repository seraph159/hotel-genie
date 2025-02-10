package com.jeff.hotel_management_system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

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

    // Override equals and hashCode for composite key
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OccupiesId that = (OccupiesId) o;
        return Objects.equals(clientEmail, that.clientEmail) &&
                Objects.equals(bookingStartDate, that.bookingStartDate) &&
                Objects.equals(bookingRoomNr, that.bookingRoomNr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientEmail, bookingStartDate, bookingRoomNr);
    }
}
