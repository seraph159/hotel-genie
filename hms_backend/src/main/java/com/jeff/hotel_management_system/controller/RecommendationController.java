package com.jeff.hotel_management_system.controller;

import com.jeff.hotel_management_system.service.RecommendationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/recommendations")
@Tag(name = "Recommendations API", description = "To use AI for recommending rooms")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @GetMapping
    @Operation(
            summary = "Recommend a room",
            description = "Uses AI to recommend the best room based on availability and client preferences.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Room recommendation generated successfully"),
            }
    )
    public ResponseEntity<String> recommendRoom(
            @Parameter(description = "Start date of the booking (format: yyyy-MM-dd)", required = true, example = "2023-10-01")
            @RequestParam String startDate,

            @Parameter(description = "End date of the booking (format: yyyy-MM-dd)", required = true, example = "2023-10-05")
            @RequestParam String endDate,

            @Parameter(description = "Minimum occupancy required for the room", required = true, example = "2")
            @RequestParam int minOccupancy,

            @Parameter(description = "Client preferences for the room (e.g., 'quiet', 'near elevator')", required = true, example = "quiet, near elevator")
            @RequestParam String preferences
    ) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return ResponseEntity.ok(recommendationService.recommendRooms(start, end, minOccupancy, preferences));
    }
}