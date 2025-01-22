package com.jeff.hotel_management_system.service;

import com.jeff.hotel_management_system.dto.AccountResponseDto;
import com.jeff.hotel_management_system.dto.AccountUpdateDto;
import com.jeff.hotel_management_system.dto.PaymentDetailsDto;
import com.jeff.hotel_management_system.entity.*;
import com.jeff.hotel_management_system.repository.AdminRepository;
import com.jeff.hotel_management_system.repository.BankAccountRepository;
import com.jeff.hotel_management_system.repository.ClientRepository;
import com.jeff.hotel_management_system.repository.CreditCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CreditCardRepository creditCardRepository;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private AdminRepository adminRepository;

    /**
     * Get account details for the current user.
     */
    public AccountResponseDto getAccountDetails(String email) {
        User user = clientRepository.findById(email)
                .orElse(null);

        if (user == null) {
            user = adminRepository.findById(email)
                    .orElseThrow(() -> new IllegalArgumentException("Account not found for email: " + email));
        }

        if (user instanceof Client) {
            Client client = (Client) user;
            // Safely handle null paymentType
            String paymentType = (client.getPaymentType() != null) ? client.getPaymentType().name() : null;

            AccountResponseDto responseDto = new AccountResponseDto(
                    client.getName(),
                    client.getEmail(),
                    client.getPhone(),
                    paymentType,
                    null, // holdername
                    null, // cardnumber
                    null, // cardType
                    null, // bank
                    null, // accountnumber
                    null // routingnumber
            );

            if ("CREDIT_CARD".equals(paymentType)) {
                creditCardRepository.findById(email).ifPresent(creditCard -> {
                    responseDto.setHoldername(creditCard.getHoldername());
                    responseDto.setCardnumber(creditCard.getCardnumber());
                    responseDto.setCardType(creditCard.getType());
                });
            } else if ("BANK_ACCOUNT".equals(paymentType)) {
                bankAccountRepository.findById(email).ifPresent(bankAccount -> {
                    responseDto.setBank(bankAccount.getBank());
                    responseDto.setAccountnumber(bankAccount.getAccountnumber());
                    responseDto.setRoutingnumber(bankAccount.getRoutingnumber());
                });
            }
            return responseDto;
        } else if (user instanceof Admin) {
            Admin admin = (Admin) user;
            // Return admin details
            return new AccountResponseDto(
                    admin.getName(),
                    admin.getEmail(),
                    admin.getPhone(), // phone
                    null, // paymentType
                    null, // holdername
                    null, // cardnumber
                    null, // cardType
                    null, // bank
                    null, // accountnumber
                    null // routingnumber
            );
        } else {
            throw new IllegalArgumentException("Unsupported user type");
        }
    }

    public AccountResponseDto updateAccount(String email, AccountUpdateDto accountUpdateDto) {
        // Check if the email corresponds to a client
        Optional<Client> clientOpt = clientRepository.findById(email);

        if (clientOpt.isPresent()) {
            Client client = clientOpt.get();

            // Update basic client fields
            client.setName(accountUpdateDto.getName());
            client.setPhone(accountUpdateDto.getPhone());

            // Validate and update payment type
            if (accountUpdateDto.getPaymentType() != null) {
                client.setPaymentType(PaymentType.valueOf(accountUpdateDto.getPaymentType()));

                // Reset payment details based on the new payment type
                if (PaymentType.CREDIT_CARD.name().equals(accountUpdateDto.getPaymentType())) {
                    creditCardRepository.deleteById(email); // Clear existing credit card details
                } else if (PaymentType.BANK_ACCOUNT.name().equals(accountUpdateDto.getPaymentType())) {
                    bankAccountRepository.deleteById(email); // Clear existing bank account details
                }
            } else {
                throw new IllegalArgumentException("Payment type is required.");
            }

            // Save updated client
            Client updatedClient = clientRepository.save(client);

            // Create response DTO
            AccountResponseDto responseDto = new AccountResponseDto(
                    updatedClient.getName(),
                    updatedClient.getEmail(),
                    updatedClient.getPhone(),
                    updatedClient.getPaymentType().name(),
                    null, // holdername
                    null, // cardnumber
                    null, // cardType
                    null, // bank
                    null, // accountnumber
                    null  // routingnumber
            );

            // Fetch updated payment details
            if ("CREDIT_CARD".equals(updatedClient.getPaymentType().name())) {
                creditCardRepository.findById(email).ifPresent(creditCard -> {
                    responseDto.setHoldername(creditCard.getHoldername());
                    responseDto.setCardnumber(creditCard.getCardnumber());
                    responseDto.setCardType(creditCard.getType());
                });
            } else if ("BANK_ACCOUNT".equals(updatedClient.getPaymentType().name())) {
                bankAccountRepository.findById(email).ifPresent(bankAccount -> {
                    responseDto.setBank(bankAccount.getBank());
                    responseDto.setAccountnumber(bankAccount.getAccountnumber());
                    responseDto.setRoutingnumber(bankAccount.getRoutingnumber());
                });
            }

            return responseDto;
        }

        // If no client was found, check for admin
        Optional<Admin> adminOpt = adminRepository.findById(email);

        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();

            // Update admin fields
            admin.setName(accountUpdateDto.getName());
            admin.setPhone(accountUpdateDto.getPhone());

            // Save updated admin
            Admin updatedAdmin = adminRepository.save(admin);

            // Create response DTO for admin
            return new AccountResponseDto(
                    updatedAdmin.getName(),
                    updatedAdmin.getEmail(),
                    updatedAdmin.getPhone(),
                    null, // Payment type not applicable for admin
                    null, // holdername
                    null, // cardnumber
                    null, // cardType
                    null, // bank
                    null, // accountnumber
                    null  // routingnumber
            );
        }

        throw new IllegalArgumentException("User not found for email: " + email);
    }



    public void updatePaymentDetails(String email, PaymentDetailsDto paymentDetailsDto) {
        Client client = clientRepository.findById(email)
                .orElseThrow(() -> new IllegalArgumentException("Client not found for email: " + email));

        if ("CREDIT_CARD".equals(paymentDetailsDto.getPaymentType())) {
            CreditCard creditCard = new CreditCard();
            creditCard.setHoldername(paymentDetailsDto.getHoldername());
            creditCard.setCardnumber(paymentDetailsDto.getCardnumber());
            creditCard.setType(paymentDetailsDto.getType());
            creditCard.setClient(client);

            creditCardRepository.save(creditCard);
        } else if ("BANK_ACCOUNT".equals(paymentDetailsDto.getPaymentType())) {
            BankAccount bankAccount = new BankAccount();
            bankAccount.setBank(paymentDetailsDto.getBank());
            bankAccount.setAccountnumber(paymentDetailsDto.getAccountnumber());
            bankAccount.setRoutingnumber(paymentDetailsDto.getRoutingnumber());
            bankAccount.setClient(client);

            bankAccountRepository.save(bankAccount);
        }
    }

    /**
     * Delete account and related records for the current user.
     */
    public void deleteAccount(String email) {
        if (clientRepository.existsById(email)) {
            creditCardRepository.deleteById(email);
            bankAccountRepository.deleteById(email);
            clientRepository.deleteById(email);
        } else {
            throw new IllegalArgumentException("Account not found for email: " + email);
        }
    }
}
