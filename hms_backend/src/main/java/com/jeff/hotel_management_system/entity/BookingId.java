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
public class BookingId implements Serializable {

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "room_nr")
    private String roomNr;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof BookingId)) return false;
        BookingId that = (BookingId) o;
        return startDate.equals(that.startDate) && roomNr.equals(that.roomNr);
    }

    @Override
    public int hashCode() {

        return Objects.hash(startDate, roomNr);
    }
}
