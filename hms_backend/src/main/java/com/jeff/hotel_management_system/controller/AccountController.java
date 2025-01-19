package com.jeff.hotel_management_system.controller;

import com.jeff.hotel_management_system.dto.AccountResponseDto;
import com.jeff.hotel_management_system.dto.AccountUpdateDto;
import com.jeff.hotel_management_system.dto.PaymentDetailsDto;
import com.jeff.hotel_management_system.utils.AuthUtils;
import com.jeff.hotel_management_system.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    //u-
    @GetMapping
    public ResponseEntity<AccountResponseDto> getAccountDetails() {
        String email = AuthUtils.getAuthenticatedEmail();
        AccountResponseDto accountResponseDto = accountService.getAccountDetails(email);
        return ResponseEntity.ok(accountResponseDto);
    }

    //u-
    @PutMapping
    public ResponseEntity<AccountResponseDto> updateAccount(@RequestBody AccountUpdateDto clientUpdateDto) {
        String email = AuthUtils.getAuthenticatedEmail();
        AccountResponseDto updatedClient = accountService.updateAccount(email, clientUpdateDto);
        return ResponseEntity.ok(updatedClient);
    }

    //u-
    @PutMapping("/payment")
    public ResponseEntity<String> updatePaymentDetails(@RequestBody PaymentDetailsDto paymentDetailsDto) {
        String email = AuthUtils.getAuthenticatedEmail();
        accountService.updatePaymentDetails(email, paymentDetailsDto);
        return ResponseEntity.ok("Payment details updated successfully");
    }

    /**
     * Delete account for the current user.
     */
    @DeleteMapping
    public ResponseEntity<?> deleteAccount() {
        String email = AuthUtils.getAuthenticatedEmail();
        accountService.deleteAccount(email);
        return ResponseEntity.ok("Account deleted successfully");
    }

}
