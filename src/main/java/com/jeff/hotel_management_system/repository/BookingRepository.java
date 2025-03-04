package com.jeff.hotel_management_system.repository;

import com.jeff.hotel_management_system.entity.Booking;
import com.jeff.hotel_management_system.entity.BookingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, BookingId> {
    // BookingId corresponds to the type of the composite primary key

    List<Booking> findByClientEmail(String email);

    // for composite key
    @Query("SELECT b FROM Booking b WHERE b.id.startDate = :startDate AND b.id.roomNr = :roomNr")
    Optional<Booking> findByStartDateAndRoomNr(
            @Param("startDate") LocalDate startDate,
            @Param("roomNr") String roomNr
    );

}