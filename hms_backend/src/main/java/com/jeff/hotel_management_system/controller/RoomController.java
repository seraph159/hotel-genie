package com.jeff.hotel_management_system.controller;

import com.jeff.hotel_management_system.dto.RoomResponseDto;
import com.jeff.hotel_management_system.entity.Room;
import com.jeff.hotel_management_system.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/rooms")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Room API", description = "Operations for managing rooms (Admin only)")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @GetMapping
    @Operation(
            summary = "Get all rooms",
            description = "Retrieve a list of all rooms",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of rooms retrieved successfully",
                            content = @Content(schema = @Schema(implementation = RoomResponseDto.class))
                    )
            }
    )
    public ResponseEntity<List<RoomResponseDto>> getAllRooms() {
        List<RoomResponseDto> rooms = roomService.getAllRooms();
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/{roomnr}")
    @Operation(
            summary = "Get room by room number",
            description = "Retrieve a room by its room number",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Room found",
                            content = @Content(schema = @Schema(implementation = Room.class))
                    )
            }
    )
    public ResponseEntity<Room> getRoomByRoomNr(
            @Parameter(description = "Room number of the room to retrieve", required = true, example = "101")
            @PathVariable String roomnr
    ) {
        return roomService.getRoomByRoomNr(roomnr)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(
            summary = "Create a new room",
            description = "Create a new room with the provided details",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Room created successfully",
                            content = @Content(schema = @Schema(implementation = Room.class))
                    )
            }
    )
    public ResponseEntity<Room> createRoom(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Room details to create",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Room.class))
            )
            @RequestBody Room room
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(roomService.createRoom(room));
    }

    @PutMapping("/{roomnr}")
    @Operation(
            summary = "Update a room",
            description = "Update an existing room by its room number",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Room updated successfully",
                            content = @Content(schema = @Schema(implementation = Room.class))
                    )
            }
    )
    public ResponseEntity<?> updateRoom(
            @Parameter(description = "Room number of the room to update", required = true, example = "101")
            @PathVariable String roomnr,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated room details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Room.class))
            )
            @RequestBody Room updatedRoom
    ) {
        return roomService.updateRoom(roomnr, updatedRoom)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{roomnr}")
    @Operation(
            summary = "Delete a room",
            description = "Delete a room by its room number",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Room deleted successfully"
                    )
            }
    )
    public ResponseEntity<String> deleteRoom(
            @Parameter(description = "Room number of the room to delete", required = true, example = "101")
            @PathVariable String roomnr
    ) {
        boolean deleted = roomService.deleteRoom(roomnr);
        return deleted ? ResponseEntity.ok("Room deleted successfully") : ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Room not found");
    }
}