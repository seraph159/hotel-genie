package com.jeff.hotel_management_system.controller;

import com.jeff.hotel_management_system.entity.Booking;
import com.jeff.hotel_management_system.entity.BookingId;
import com.jeff.hotel_management_system.service.BookingService;
import com.jeff.hotel_management_system.service.StripeService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/stripe")
@Tag(name = "Stripe API", description = "Operations related to Stripe payment processing")
public class StripeController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private StripeService stripeService;

    @Value("${stripe.secretWebHookKey}")
    private String stripeWebHookKey;

    @PostMapping("/webhook")
    @Operation(
            summary = "Handle Stripe webhook events",
            description = "Processes Stripe webhook events, such as successful payments",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Webhook processed successfully",
                            content = @Content(schema = @Schema(implementation = String.class))
                    )
            }
    )
    public ResponseEntity<?> handleStripeWebhook(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Raw payload from Stripe webhook",
                    required = true,
                    content = @Content(schema = @Schema(implementation = String.class))
            )
            @RequestBody String payload,

            @Parameter(
                    description = "Stripe-Signature header for verifying webhook authenticity",
                    required = true,
                    example = "t=123456789,v1=abc123"
            )
            @RequestHeader("Stripe-Signature") String sigHeader
    ) {
        String endpointSecret = stripeWebHookKey; // Replace with your webhook secret
        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }

        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);

            if (session != null) {
                // Retrieve booking information from session metadata
                String roomNr = session.getMetadata().get("roomNr");
                String startDate = session.getMetadata().get("startDate");
                String endDate = session.getMetadata().get("endDate");
                String emailUser = session.getMetadata().get("emailUser");
                Long price = Long.valueOf(session.getAmountTotal()) / 100; // Convert cents to dollars

                // Create booking object
                Booking booking = new Booking();
                booking.setId(new BookingId(LocalDate.parse(startDate), roomNr));
                booking.setPrice(price);
                booking.setEndDate(LocalDate.parse(endDate));

                // Save the booking in the database
                bookingService.createBooking(booking, emailUser, roomNr);
            }
        }

        return ResponseEntity.ok("Webhook processed");
    }

    @GetMapping("/get-session")
    @Operation(
            summary = "Get Stripe session details",
            description = "Retrieves details of a Stripe checkout session by session ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Session details retrieved successfully",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Failed to retrieve session details"
                    )
            }
    )
    public ResponseEntity<?> getSessionDetails(
            @Parameter(
                    description = "Stripe session ID",
                    required = true,
                    example = "cs_test_a1b2c3d4e5f6g7h8i9j0"
            )
            @RequestParam("session_id") String sessionId
    ) {
        try {
            // Retrieve session details from Stripe
            Session session = stripeService.getSessionDetails(sessionId);

            // Create a map/Dto with the required fields
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("amount_total", session.getAmountTotal());
            responseData.put("metadata", session.getMetadata());
            responseData.put("payment_status", session.getPaymentStatus());
            responseData.put("status", session.getStatus());

            return ResponseEntity.ok(responseData);
        } catch (StripeException e) {
            // Handle errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve session details: " + e.getMessage());
        }
    }
}