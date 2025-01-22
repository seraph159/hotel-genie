package com.jeff.hotel_management_system.controller;

import com.jeff.hotel_management_system.entity.Occupies;
import com.jeff.hotel_management_system.entity.OccupiesId;
import com.jeff.hotel_management_system.service.OccupiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/occupies")
public class OccupiesController {

    @Autowired
    private OccupiesService occupiesService;

    @GetMapping
    public List<Occupies> getAllOccupies() {

        return occupiesService.getAllOccupies();
    }

    @GetMapping("/{clientEmail}/{startDate}/{roomNr}")
    public ResponseEntity<Occupies> getOccupiesById(
            @PathVariable String clientEmail,
            @PathVariable LocalDate startDate,
            @PathVariable String roomNr) {

        OccupiesId occupiesId = new OccupiesId(clientEmail, startDate, roomNr);
        return occupiesService.getOccupiesById(occupiesId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Occupies createOccupies(@RequestBody Occupies occupies) {

        return occupiesService.createOccupies(occupies);
    }

    @DeleteMapping("/{clientEmail}/{startDate}/{roomNr}")
    public ResponseEntity<Void> deleteOccupies(
            @PathVariable String clientEmail,
            @PathVariable LocalDate startDate,
            @PathVariable String roomNr) {

        OccupiesId occupiesId = new OccupiesId(clientEmail, startDate, roomNr);
        boolean deleted = occupiesService.deleteOccupies(occupiesId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
