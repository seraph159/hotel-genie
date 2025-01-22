package com.jeff.hotel_management_system.repository;

import com.jeff.hotel_management_system.entity.Booking;
import com.jeff.hotel_management_system.entity.BookingId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, BookingId> {
    // BookingId corresponds to the type of the composite primary key

    List<Booking> findByClientEmail(String email);

}