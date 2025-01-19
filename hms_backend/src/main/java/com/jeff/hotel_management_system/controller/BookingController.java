package com.jeff.hotel_management_system.controller;

import com.jeff.hotel_management_system.dto.BookingDto;
import com.jeff.hotel_management_system.dto.CheckoutRequest;
import com.jeff.hotel_management_system.dto.CheckoutResponse;
import com.jeff.hotel_management_system.entity.Booking;
import com.jeff.hotel_management_system.entity.BookingId;
import com.jeff.hotel_management_system.utils.AuthUtils;
import com.jeff.hotel_management_system.service.BookingService;
import com.jeff.hotel_management_system.service.PricingService;
import com.jeff.hotel_management_system.service.StripeService;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private StripeService stripeService;

    @Autowired
    private PricingService pricingService;

    // Admin-only endpoint
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<BookingDto> getAllBookings() {

        return bookingService.getAllBookings();
    }

    // Admin-only endpoint
    @GetMapping("/{startDate}/{roomNr}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Booking> getBooking(@PathVariable LocalDate startDate, @PathVariable String roomNr) {

        BookingId bookingId = new BookingId(startDate, roomNr);
        return bookingService.getBookingById(bookingId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getClientBookings() {

        List<Booking> bookings = bookingService.findByClientEmail(AuthUtils.getAuthenticatedEmail());
        List<BookingDto> bookingDTOs = bookings.stream()
                .map(booking -> new BookingDto(
                        booking.getPrice(),
                        booking.getId().getStartDate(),
                        booking.getEndDate(),
                        booking.getId().getRoomNr(),
                        booking.getClient().getEmail(),
                        booking.getRoom()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(bookingDTOs);
    }

    @PostMapping
    public ResponseEntity<CheckoutResponse> createBooking(@RequestBody Booking booking) throws StripeException {

        // Calculate the price
        Long price = pricingService.calculatePrice(booking.getId().getRoomNr(), booking.getId().getStartDate(), booking.getEndDate());

        // Create Stripe Checkout session
        CheckoutRequest checkoutRequest = new CheckoutRequest();
        checkoutRequest.setAmount(price * 100); // Stripe expects amounts in cents
        checkoutRequest.setSuccessUrl("http://localhost:3000/success"); // Frontend success URL
        checkoutRequest.setCancelUrl("http://localhost:3000/cancel");   // Frontend cancel URL

        CheckoutResponse checkoutResponse = stripeService.createCheckoutSession(booking.getId().getRoomNr(), booking.getId().getStartDate(), booking.getEndDate(), checkoutRequest);

        return ResponseEntity.ok(checkoutResponse);
    }

    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createBookingAdmin(@RequestBody BookingDto booking) {

        Booking newBooking = bookingService.createBookingAdmin(booking);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Booking successfully created with ID: " + newBooking.getId().hashCode());
    }

    @PutMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateBookingAdmin(@RequestBody BookingDto bookingDto) {

        Booking updatedBooking = bookingService.updateBooking(bookingDto);
        return ResponseEntity.ok("Booking successfully updated with ID: " + updatedBooking.getId().hashCode());
    }

    @DeleteMapping("/admin/{startDate}/{roomNr}")
    public ResponseEntity<Void> deleteBookingAdmin(@PathVariable LocalDate startDate, @PathVariable String roomNr) {

        BookingId bookingId = new BookingId(startDate, roomNr);
        boolean deleted = bookingService.deleteBookingAdmin(bookingId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }


    @DeleteMapping("/{startDate}/{roomNr}")
    public ResponseEntity<Void> deleteBooking(@PathVariable LocalDate startDate, @PathVariable String roomNr) {

        BookingId bookingId = new BookingId(startDate, roomNr);
        boolean deleted = bookingService.deleteBooking(bookingId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}