package com.jeff.hotel_management_system.service;

import com.jeff.hotel_management_system.dto.RoomDto;
import com.jeff.hotel_management_system.dto.RoomResponseDto;
import com.jeff.hotel_management_system.entity.Room;
import com.jeff.hotel_management_system.repository.RoomRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private PricingService pricingService;

    public List<RoomResponseDto> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private RoomResponseDto mapToDto(Room room) {
        RoomResponseDto dto = new RoomResponseDto();
        dto.setRoomNr(room.getRoomNr());
        dto.setFloor(room.getFloor());
        dto.setMaxOccupancy(room.getMaxOccupancy());
        dto.setAvailable(room.isAvailable());
        dto.setBasePrice(room.getBasePrice());
        dto.setRoomType(room.getRoomType());
        dto.setHasSeaView(room.isHasSeaView());
        dto.setHasBalcony(room.isHasBalcony());
        dto.setHasWifi(room.isHasWifi());
        dto.setHasAirConditioning(room.isHasAirConditioning());
        dto.setPetFriendly(room.isPetFriendly());
        dto.setAmenities(room.getAmenities());
        dto.setRating(room.getRating());
        dto.setPreferredFor(room.getPreferredFor());
        return dto;
    }

    public Optional<Room> getRoomByRoomNr(String roomNr) {

        return roomRepository.findById(roomNr);
    }

    public Room createRoom(Room room) {

        return roomRepository.save(room);
    }

    public Optional<Room> updateRoom(String roomNr, Room updatedRoom) {
        return roomRepository.findById(roomNr).map(existingRoom -> {
            updatedRoom.setRoomNr(roomNr);
            return roomRepository.save(updatedRoom);
        });
    }

    public boolean deleteRoom(String roomNr) {

        return roomRepository.findById(roomNr).map(room -> {
            roomRepository.delete(room);
            return true;
        }).orElse(false);
    }

    public List<RoomDto> availableRooms(LocalDate startDate, LocalDate endDate, int minOccupancy){

        List<Room> availableRooms = roomRepository.findAvailableRooms(startDate, endDate, minOccupancy);

        List<RoomDto> roomDTOs = new ArrayList<>();

        // Calculate price for each room based on dates
        availableRooms.forEach(room -> {
            double price = pricingService.calculatePrice(room, startDate, endDate);
            RoomDto roomDto = new RoomDto();
            roomDto.setRoom(room);
            roomDto.setPrice(price);
            roomDTOs.add(roomDto);
        });

        return roomDTOs;
    }

}
