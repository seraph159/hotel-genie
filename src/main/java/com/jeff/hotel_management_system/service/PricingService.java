package com.jeff.hotel_management_system.service;

import com.jeff.hotel_management_system.entity.Room;
import com.jeff.hotel_management_system.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class PricingService {

    @Autowired
    private RoomRepository roomRepository;

    public Long calculatePrice(String roomNr, LocalDate startDate, LocalDate endDate) {
        return roomRepository.findByRoomNr(roomNr)
                .map(room -> {
                    Long basePrice = room.getBasePrice();
                    Long price = basePrice * (endDate.toEpochDay() - startDate.toEpochDay());
                    return price;
                })
                .orElseThrow(() -> new RuntimeException("Room not found"));
    }

    public Long calculatePrice(Room room, LocalDate startDate, LocalDate endDate) {
        // Calculate price based on room type, dates, and occupancy
        // For simplicity, let's assume a basic calculation
        Long basePrice = room.getBasePrice();
        Long price = basePrice * (endDate.toEpochDay() - startDate.toEpochDay());
        return price;
    }
}