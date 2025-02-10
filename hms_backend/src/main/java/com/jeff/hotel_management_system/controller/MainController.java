package com.jeff.hotel_management_system.controller;

import com.jeff.hotel_management_system.dto.RoomDto;
import com.jeff.hotel_management_system.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/main")
@Tag(name = "Main API", description = "Operations related to room availability")
public class MainController {

    @Autowired
    private RoomService roomService;

    @GetMapping("/available-rooms")
    @Operation(
            summary = "Get available rooms",
            description = "Retrieve a list of available rooms based on the provided criteria",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of available rooms retrieved successfully",
                            content = @Content(schema = @Schema(implementation = RoomDto.class))
                    )
            }
    )
    public ResponseEntity<?> availableRooms(
            @Parameter(
                    description = "Start date for availability (format: yyyy-MM-dd)",
                    required = true,
                    example = "2023-12-01"
            )
            @RequestParam LocalDate startDate,

            @Parameter(
                    description = "End date for availability (format: yyyy-MM-dd)",
                    required = true,
                    example = "2023-12-10"
            )
            @RequestParam LocalDate endDate,

            @Parameter(
                    description = "Minimum occupancy required for the room",
                    required = true,
                    example = "2"
            )
            @RequestParam int minOccupancy
    ) {
        List<RoomDto> availableRooms = roomService.availableRooms(startDate, endDate, minOccupancy);
        return ResponseEntity.ok(availableRooms);
    }
}