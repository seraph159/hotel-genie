package com.jeff.hotel_management_system.service;

import com.jeff.hotel_management_system.dto.CheckoutRequest;
import com.jeff.hotel_management_system.dto.CheckoutResponse;
import com.jeff.hotel_management_system.utils.AuthUtils;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;


@Service
public class StripeService {

    @Value("${stripe.secretKey}")
    private String stripeApiKey;

    public CheckoutResponse createCheckoutSession(String roomNr, LocalDate startDate, LocalDate endDate,CheckoutRequest checkoutRequest) throws StripeException {
        Stripe.apiKey = stripeApiKey;
        String emailUser = AuthUtils.getAuthenticatedEmail();

        // Create Checkout Session parameters
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(checkoutRequest.getSuccessUrl() + "?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(checkoutRequest.getCancelUrl())
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmount(checkoutRequest.getAmount())
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Room Booking")
                                                                .build())
                                                .build())
                                .build())
                .putMetadata("emailUser", emailUser)
                .putMetadata("roomNr", roomNr)
                .putMetadata("startDate", String.valueOf(startDate))
                .putMetadata("endDate", String.valueOf(endDate))
                .build();

        // Create the session
        Session session = null;
        CheckoutResponse response = new CheckoutResponse();
        try {
            session = Session.create(params);
            response.setCheckoutId(session.getId());
            response.setStatus("SUCCESS");
            response.setCheckoutLink(session.getUrl());
            response.setMessage("Session created");

        } catch (StripeException e) {
            System.out.println("Error creating Stripe session: {}"+ e.getMessage());

            // Return an error response
            CheckoutResponse errorResponse = new CheckoutResponse();
            errorResponse.setStatus("failure");
            errorResponse.setMessage("Failed to create Stripe session");
            return errorResponse;
        }

        return response;
    }

    /**
     * Retrieves the Stripe Checkout session details by session ID.
     *
     * @param sessionId The ID of the Stripe session to retrieve
     * @return The retrieved Stripe session
     * @throws StripeException If the session retrieval fails
     */
    public Session getSessionDetails(String sessionId) throws StripeException {
        Stripe.apiKey = stripeApiKey;
        return Session.retrieve(sessionId);
    }
}

