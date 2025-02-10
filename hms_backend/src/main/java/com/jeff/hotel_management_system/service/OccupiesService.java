package com.jeff.hotel_management_system.service;

import com.jeff.hotel_management_system.entity.Booking;
import com.jeff.hotel_management_system.entity.Client;
import com.jeff.hotel_management_system.entity.Occupies;
import com.jeff.hotel_management_system.entity.OccupiesId;
import com.jeff.hotel_management_system.exception.ResourceNotFoundException;
import com.jeff.hotel_management_system.repository.BookingRepository;
import com.jeff.hotel_management_system.repository.ClientRepository;
import com.jeff.hotel_management_system.repository.OccupiesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class OccupiesService {

    @Autowired
    private OccupiesRepository occupiesRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private BookingRepository bookingRepository;

    public List<Occupies> getAllOccupies() {
        return occupiesRepository.findAll();
    }

    public Optional<Occupies> getOccupiesById(OccupiesId occupiesId) {
        return occupiesRepository.findById(occupiesId);
    }

    public Occupies createOccupies(Occupies occupies) {
        return occupiesRepository.save(occupies);
    }

    public boolean deleteOccupies(OccupiesId occupiesId) {
        return occupiesRepository.findById(occupiesId).map(occupies -> {
            occupiesRepository.delete(occupies);
            return true;
        }).orElse(false);
    }

    public Client getClientByEmail(String email) {
        return clientRepository.findById(email)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with email: " + email));
    }

    public Booking getBookingByStartDateAndRoomNr(LocalDate startDate, String roomNr) {
        return bookingRepository.findByStartDateAndRoomNr(startDate, roomNr)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with start date: " + startDate + " and room number: " + roomNr));
    }
}