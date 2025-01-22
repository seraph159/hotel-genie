package com.jeff.hotel_management_system.controller;

import com.jeff.hotel_management_system.dto.RoomDto;
import com.jeff.hotel_management_system.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/main")
public class MainController {

    @Autowired
    private RoomService roomService;

    //u-
    @GetMapping("/available-rooms")
    public ResponseEntity<List<RoomDto>> availableRooms(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam int minOccupancy
    ) {
        List<RoomDto> availableRooms = roomService.availableRooms(startDate, endDate, minOccupancy);
        return ResponseEntity.ok(availableRooms);
    }
}