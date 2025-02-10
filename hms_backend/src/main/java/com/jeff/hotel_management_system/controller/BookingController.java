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


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/bookings")
@Tag(name = "Booking API", description = "Operations related to booking management")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private StripeService stripeService;

    @Autowired
    private PricingService pricingService;

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get all bookings (Admin only)",
            description = "Retrieves a list of all bookings. Requires ADMIN role.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of all bookings retrieved successfully",
                            content = @Content(schema = @Schema(implementation = BookingDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - Access denied"
                    )
            }
    )
    public List<BookingDto> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/{startDate}/{roomNr}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get booking by start date and room number (Admin only)",
            description = "Retrieves a booking by its start date and room number. Requires ADMIN role.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Booking retrieved successfully",
                            content = @Content(schema = @Schema(implementation = Booking.class))
                    )
            }
    )
    public ResponseEntity<?> getBooking(
            @Parameter(description = "Start date of the booking (format: yyyy-MM-dd)", required = true, example = "2023-12-01")
            @PathVariable LocalDate startDate,

            @Parameter(description = "Room number of the booking", required = true, example = "101")
            @PathVariable String roomNr
    ) {
        BookingId bookingId = new BookingId(startDate, roomNr);
        return bookingService.getBookingById(bookingId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(
            summary = "Get bookings for the authenticated client",
            description = "Retrieves a list of bookings for the currently authenticated client.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of bookings retrieved successfully",
                            content = @Content(schema = @Schema(implementation = BookingDto.class))
                    )
            }
    )
    public ResponseEntity<?> getClientBookings() {
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
    @Operation(
            summary = "Create a new booking",
            description = "Creates a new booking and initiates a Stripe Checkout session for payment.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Booking created and Stripe Checkout session initiated successfully",
                            content = @Content(schema = @Schema(implementation = CheckoutResponse.class))
                    )
            }
    )
    public ResponseEntity<?> createBooking(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Booking details to create",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Booking.class))
            )
            @RequestBody Booking booking
    ) throws StripeException {
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
    @Operation(
            summary = "Create a new booking (Admin only)",
            description = "Creates a new booking as an admin. Requires ADMIN role.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Booking created successfully",
                            content = @Content(schema = @Schema(implementation = String.class))
                    )
            }
    )
    public ResponseEntity<?> createBookingAdmin(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Booking details to create",
                    required = true,
                    content = @Content(schema = @Schema(implementation = BookingDto.class))
            )
            @RequestBody BookingDto booking
    ) {
        Booking newBooking = bookingService.createBookingAdmin(booking);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Booking successfully created with ID: " + newBooking.getId().hashCode());
    }

    @PutMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update a booking (Admin only)",
            description = "Updates an existing booking as an admin. Requires ADMIN role.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Booking updated successfully",
                            content = @Content(schema = @Schema(implementation = String.class))
                    )
            }
    )
    public ResponseEntity<?> updateBookingAdmin(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated booking details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = BookingDto.class))
            )
            @RequestBody BookingDto bookingDto
    ) {
        Booking updatedBooking = bookingService.updateBooking(bookingDto);
        return ResponseEntity.ok("Booking successfully updated with ID: " + updatedBooking.getId().hashCode());
    }

    @DeleteMapping("/admin/{startDate}/{roomNr}")
    @Operation(
            summary = "Delete a booking (Admin only)",
            description = "Deletes a booking by its start date and room number. Requires ADMIN role.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Booking deleted successfully"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Booking not found"
                    )
            }
    )
    public ResponseEntity<?> deleteBookingAdmin(
            @Parameter(description = "Start date of the booking (format: yyyy-MM-dd)", required = true, example = "2023-12-01")
            @PathVariable LocalDate startDate,

            @Parameter(description = "Room number of the booking", required = true, example = "101")
            @PathVariable String roomNr
    ) {
        BookingId bookingId = new BookingId(startDate, roomNr);
        boolean deleted = bookingService.deleteBookingAdmin(bookingId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{startDate}/{roomNr}")
    @Operation(
            summary = "Delete a booking",
            description = "Deletes a booking by its start date and room number.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Booking deleted successfully"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Booking not found"
                    )
            }
    )
    public ResponseEntity<?> deleteBooking(
            @Parameter(description = "Start date of the booking (format: yyyy-MM-dd)", required = true, example = "2023-12-01")
            @PathVariable LocalDate startDate,

            @Parameter(description = "Room number of the booking", required = true, example = "101")
            @PathVariable String roomNr
    ) {
        BookingId bookingId = new BookingId(startDate, roomNr);
        boolean deleted = bookingService.deleteBooking(bookingId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}