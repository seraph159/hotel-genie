package com.jeff.hotel_management_system.repository;

import com.jeff.hotel_management_system.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, String> {


        @Query("SELECT r FROM Room r WHERE r.maxOccupancy >= :minOccupancy AND r.roomNr NOT IN " +
                "(SELECT b.id.roomNr FROM Booking b WHERE b.id.startDate < :endDate AND b.endDate > :startDate)")
        List<Room> findAvailableRooms(@Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate,
                                      @Param("minOccupancy") int minOccupancy);


        Optional<Room> findByRoomNr(String roomNr);
}
