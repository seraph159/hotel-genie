package com.jeff.hotel_management_system.controller;

import com.jeff.hotel_management_system.dto.OccupiesDto;
import com.jeff.hotel_management_system.entity.Booking;
import com.jeff.hotel_management_system.entity.Client;
import com.jeff.hotel_management_system.entity.Occupies;
import com.jeff.hotel_management_system.entity.OccupiesId;
import com.jeff.hotel_management_system.mapper.OccupiesMapper;
import com.jeff.hotel_management_system.service.OccupiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/occupies")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Occupies API", description = "Operations for managing room occupancy records. " +
        "There may be zero or more clients that will be staying in the room for a booking (Admin only)")
public class OccupiesController {

    @Autowired
    private OccupiesService occupiesService;

    @Autowired
    private OccupiesMapper occupiesMapper;

    @GetMapping
    @Operation(
            summary = "Get all occupancy records",
            description = "Retrieve a list of all occupancy records",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved all occupancy records"
                    )
            }
    )
    public List<OccupiesDto> getAllOccupies() {
        return occupiesService.getAllOccupies().stream()
                .map(occupiesMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{clientEmail}/{startDate}/{roomNr}")
    @Operation(
            summary = "Get an occupancy record by ID",
            description = "Retrieve an occupancy record by client email, start date, and room number",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the occupancy record",
                            content = @Content(schema = @Schema(implementation = OccupiesDto.class))
                    )
            }
    )
    public ResponseEntity<?> getOccupiesById(
            @Parameter(description = "Client email", required = true, example = "client@example.com")
            @PathVariable String clientEmail,

            @Parameter(description = "Start date of the booking", required = true, example = "2023-10-01")
            @PathVariable LocalDate startDate,

            @Parameter(description = "Room number", required = true, example = "101")
            @PathVariable String roomNr) {

        OccupiesId occupiesId = new OccupiesId(clientEmail, startDate, roomNr);
        return occupiesService.getOccupiesById(occupiesId)
                .map(occupiesMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(
            summary = "Create a new occupancy record",
            description = "Create a new occupancy record with the provided details",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully created the occupancy record",
                            content = @Content(schema = @Schema(implementation = OccupiesDto.class))
                    )
            }
    )
    public ResponseEntity<?> createOccupies(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Occupancy record details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = OccupiesDto.class))
            )
            @RequestBody OccupiesDto occupiesDto) {

        // Fetch the Client and Booking entities based on the DTO
        Client client = occupiesService.getClientByEmail(occupiesDto.getClientEmail());
        Booking booking = occupiesService.getBookingByStartDateAndRoomNr(
                occupiesDto.getBookingStartDate(),
                occupiesDto.getBookingRoomNr()
        );

        // Convert DTO to Entity
        Occupies occupies = occupiesMapper.toEntity(occupiesDto, client, booking);

        // Save the entity
        Occupies savedOccupies = occupiesService.createOccupies(occupies);

        // Return the saved entity as a DTO
        return ResponseEntity.ok(occupiesMapper.toDTO(savedOccupies));
    }

    @DeleteMapping("/{clientEmail}/{startDate}/{roomNr}")
    @Operation(
            summary = "Delete an occupancy record",
            description = "Delete an occupancy record by client email, start date, and room number",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successfully deleted the occupancy record"
                    )
            }
    )
    public ResponseEntity<Void> deleteOccupies(
            @Parameter(description = "Client email", required = true, example = "client@example.com")
            @PathVariable String clientEmail,

            @Parameter(description = "Start date of the booking", required = true, example = "2023-10-01")
            @PathVariable LocalDate startDate,

            @Parameter(description = "Room number", required = true, example = "101")
            @PathVariable String roomNr) {

        OccupiesId occupiesId = new OccupiesId(clientEmail, startDate, roomNr);
        boolean deleted = occupiesService.deleteOccupies(occupiesId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}