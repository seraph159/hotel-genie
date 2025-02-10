package com.jeff.hotel_management_system.controller;

import com.jeff.hotel_management_system.dto.AccountResponseDto;
import com.jeff.hotel_management_system.dto.AccountUpdateDto;
import com.jeff.hotel_management_system.dto.PaymentDetailsDto;
import com.jeff.hotel_management_system.utils.AuthUtils;
import com.jeff.hotel_management_system.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/account")
@Tag(name = "Account API", description = "Operations related to user account management")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping
    @Operation(
            summary = "Get account details",
            description = "Retrieves account details for the authenticated user",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Account details retrieved successfully",
                            content = @Content(schema = @Schema(implementation = AccountResponseDto.class))
                    )
            }
    )
    public ResponseEntity<?> getAccountDetails() {
        String email = AuthUtils.getAuthenticatedEmail();
        AccountResponseDto accountResponseDto = accountService.getAccountDetails(email);
        return ResponseEntity.ok(accountResponseDto);
    }

    @PutMapping
    @Operation(
            summary = "Update account details",
            description = "Updates account details for the authenticated user",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Account details updated successfully",
                            content = @Content(schema = @Schema(implementation = AccountResponseDto.class))
                    )
            }
    )
    public ResponseEntity<?> updateAccount(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated account details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AccountUpdateDto.class))
            )
            @RequestBody AccountUpdateDto clientUpdateDto
    ) {
        String email = AuthUtils.getAuthenticatedEmail();
        AccountResponseDto updatedClient = accountService.updateAccount(email, clientUpdateDto);
        return ResponseEntity.ok(updatedClient);
    }

    @PutMapping("/payment")
    @Operation(
            summary = "Update payment details",
            description = "Updates payment details for the authenticated user",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Payment details updated successfully",
                            content = @Content(schema = @Schema(implementation = String.class))
                    )
            }
    )
    public ResponseEntity<?> updatePaymentDetails(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated payment details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PaymentDetailsDto.class))
            )
            @RequestBody PaymentDetailsDto paymentDetailsDto
    ) {
        String email = AuthUtils.getAuthenticatedEmail();
        accountService.updatePaymentDetails(email, paymentDetailsDto);
        return ResponseEntity.ok("Payment details updated successfully");
    }

    @DeleteMapping
    @Operation(
            summary = "Delete account",
            description = "Deletes the account for the authenticated user",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Account deleted successfully",
                            content = @Content(schema = @Schema(implementation = String.class))
                    )
            }
    )
    public ResponseEntity<?> deleteAccount() {
        String email = AuthUtils.getAuthenticatedEmail();
        accountService.deleteAccount(email);
        return ResponseEntity.ok("Account deleted successfully");
    }
}