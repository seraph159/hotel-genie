package com.jeff.hotel_management_system.controller;

import com.jeff.hotel_management_system.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @GetMapping
    public String recommendRoom(@RequestParam String startDate,
                                @RequestParam String endDate,
                                @RequestParam int minOccupancy,
                                @RequestParam String preferences) {

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return recommendationService.recommendRooms(start, end, minOccupancy, preferences);
    }
}