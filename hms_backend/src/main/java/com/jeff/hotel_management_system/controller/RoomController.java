package com.jeff.hotel_management_system.controller;

import com.jeff.hotel_management_system.dto.RoomResponseDto;
import com.jeff.hotel_management_system.entity.Room;
import com.jeff.hotel_management_system.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @GetMapping
    public ResponseEntity<List<RoomResponseDto>> getAllRooms() {

        List<RoomResponseDto> rooms = roomService.getAllRooms();
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/{roomnr}")
    public ResponseEntity<Room> getRoomByRoomNr(@PathVariable String roomnr) {

        return roomService.getRoomByRoomNr(roomnr)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Room> createRoom(@RequestBody Room room) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(roomService.createRoom(room));
    }

    @PutMapping("/{roomnr}")
    public ResponseEntity<Room> updateRoom(@PathVariable String roomnr, @RequestBody Room updatedRoom) {

        return roomService.updateRoom(roomnr, updatedRoom)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{roomnr}")
    public ResponseEntity<String> deleteRoom(@PathVariable String roomnr) {

        boolean deleted = roomService.deleteRoom(roomnr);
        return deleted ? ResponseEntity.ok("Room deleted successfully") : ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Room not found");
    }
}