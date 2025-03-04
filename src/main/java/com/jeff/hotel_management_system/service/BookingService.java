package com.jeff.hotel_management_system.service;

import com.jeff.hotel_management_system.dto.BookingDto;
import com.jeff.hotel_management_system.entity.*;
import com.jeff.hotel_management_system.repository.BookingRepository;
import com.jeff.hotel_management_system.repository.ClientRepository;
import com.jeff.hotel_management_system.repository.OccupiesRepository;
import com.jeff.hotel_management_system.repository.RoomRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private OccupiesRepository occupiesRepository;

    @Autowired
    private PricingService pricingService;

    @Autowired
    private RoomRepository roomRepository;

    public List<BookingDto> getAllBookings() {

        return bookingRepository.findAll().stream()
                .map(booking -> new BookingDto(
                        booking.getPrice(),
                        booking.getId().getStartDate(),
                        booking.getEndDate(),
                        booking.getId().getRoomNr(),
                        booking.getClient().getEmail(),
                        booking.getRoom()
                ))
                .collect(Collectors.toList());
    }

    public Optional<Booking> getBookingById(BookingId bookingId) {

        return bookingRepository.findById(bookingId);
    }

    public List<Booking> findByClientEmail(String email) {

        return bookingRepository.findByClientEmail(email);
    }


    public Booking createBooking(Booking booking, String clientEmail, String roomNr) {

        String userEmail = clientEmail;

        // Fetch the client from the database
        Client client = clientRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Client not found for email: " + userEmail));

        // Associate the client with the booking
        booking.setClient(client);

        // Retrieve Room
        Room room = roomRepository.findById(roomNr)
                .orElseThrow(() -> new EntityNotFoundException("Room not found with number: " + roomNr));

        booking.setRoom(room);

        // Save the booking
        Booking savedBooking = bookingRepository.save(booking);

        // Create an Occupies entry
        OccupiesId occupiesId = new OccupiesId(
                client.getEmail(),
                savedBooking.getId().getStartDate(),
                savedBooking.getId().getRoomNr()
        );
        Occupies occupies = new Occupies(occupiesId, client, savedBooking);

        // Save the Occupies entry
        occupiesRepository.save(occupies);

        return savedBooking;
    }

    public Booking createBookingAdmin(BookingDto bookingDto) {

        // Retrieve Room
        Room room = roomRepository.findById(bookingDto.getRoomNr())
                .orElseThrow(() -> new EntityNotFoundException("Room not found with number: " + bookingDto.getRoomNr()));

        // Retrieve Client
        Client client = clientRepository.findById(bookingDto.getClientEmail())
                .orElseThrow(() -> new EntityNotFoundException("Client not found with email: " + bookingDto.getClientEmail()));

        // Calculate the price
        Long price = pricingService.calculatePrice(bookingDto.getRoomNr(), bookingDto.getStartDate(), bookingDto.getEndDate());

        // Create and populate Booking entity
        Booking newBooking = new Booking();
        newBooking.setId(new BookingId(bookingDto.getStartDate(), bookingDto.getRoomNr()));
        newBooking.setPrice(price);
        newBooking.setEndDate(bookingDto.getEndDate());
        newBooking.setRoom(room);
        newBooking.setClient(client);

        // Save Booking to the database
        return bookingRepository.save(newBooking);
    }

    public Booking updateBooking(BookingDto bookingDto) {
        // Retrieve existing Booking by its composite key
        BookingId bookingId = new BookingId(bookingDto.getStartDate(), bookingDto.getRoomNr());
        Booking existingBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with ID: " + bookingId.hashCode()));

        // Update mutable fields
        existingBooking.setEndDate(bookingDto.getEndDate());

        // Recalculate price (optional, if price is derived from other fields)
        Long updatedPrice = pricingService.calculatePrice(bookingDto.getRoomNr(), bookingDto.getStartDate(), bookingDto.getEndDate());
        existingBooking.setPrice(updatedPrice);

        // Save and return the updated booking
        return bookingRepository.save(existingBooking);
    }


    public boolean deleteBooking(BookingId bookingId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        return bookingRepository.findById(bookingId).map(booking -> {
            // Validate that the booking belongs to the current user
            if (!booking.getClient().getEmail().equals(userEmail)) {
                throw new AccessDeniedException("You are not authorized to delete this booking.");
            }

            // Delete related Occupies entries
            occupiesRepository.findById(new OccupiesId(userEmail, bookingId.getStartDate(), bookingId.getRoomNr()))
                    .ifPresent(occupiesRepository::delete); // Call delete directly

            // Delete the Booking
            bookingRepository.delete(booking);
            return true;
        }).orElse(false);
    }


    public boolean deleteBookingAdmin(BookingId bookingId) {
        // Check if the booking exists
        if (bookingRepository.existsById(bookingId)) {
            // Delete the booking
            bookingRepository.deleteById(bookingId);
            return true;
        }
        return false; // Booking not found
    }

}
