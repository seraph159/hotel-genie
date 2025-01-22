package com.jeff.hotel_management_system.service;

import com.jeff.hotel_management_system.dto.AccountResponseDto;
import com.jeff.hotel_management_system.dto.AccountUpdateDto;
import com.jeff.hotel_management_system.dto.PaymentDetailsDto;
import com.jeff.hotel_management_system.entity.*;
import com.jeff.hotel_management_system.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private CreditCardRepository creditCardRepository;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private AdminRepository adminRepository;

    @InjectMocks
    private AccountService accountService;

    private Client client;
    private Admin admin;
    private CreditCard creditCard;
    private BankAccount bankAccount;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        client = new Client("1234567890", PaymentType.CREDIT_CARD);
        client.setName("John Doe");
        client.setEmail("client@email.com");
        client.setPassword("password");
        client.setRole("CLIENT");
        admin = new Admin();
        admin.setName("Admin Name");
        admin.setEmail("admin@email.com");
        admin.setPassword("password");
        admin.setRole("ADMIN");
        creditCard = new CreditCard("client@email.com", "John Doe", "1234567890123456", "VISA", client);
        bankAccount = new BankAccount("client@email.com", "Bank of Example", "123456789", "012345678", client);
    }

    @Test
    void testGetAccountDetails_ForClientWithCreditCard() {
        given(clientRepository.findById("client@email.com")).willReturn(Optional.of(client));
        given(creditCardRepository.findById("client@email.com")).willReturn(Optional.of(creditCard));

        AccountResponseDto response = accountService.getAccountDetails("client@email.com");

        assertThat(response.getName()).isEqualTo("John Doe");
        assertThat(response.getEmail()).isEqualTo("client@email.com");
        assertThat(response.getPaymentType()).isEqualTo("CREDIT_CARD");
        assertThat(response.getHoldername()).isEqualTo("John Doe");
        assertThat(response.getCardnumber()).isEqualTo("1234567890123456");
        assertThat(response.getCardType()).isEqualTo("VISA");
        assertThat(response.getBank()).isNull();
        assertThat(response.getAccountnumber()).isNull();
        assertThat(response.getRoutingnumber()).isNull();
    }

    @Test
    void testGetAccountDetails_ForClientWithBankAccount() {
        client.setPaymentType(PaymentType.BANK_ACCOUNT);
        given(clientRepository.findById("client@email.com")).willReturn(Optional.of(client));
        given(bankAccountRepository.findById("client@email.com")).willReturn(Optional.of(bankAccount));

        AccountResponseDto response = accountService.getAccountDetails("client@email.com");

        assertThat(response.getName()).isEqualTo("John Doe");
        assertThat(response.getEmail()).isEqualTo("client@email.com");
        assertThat(response.getPaymentType()).isEqualTo("BANK_ACCOUNT");
        assertThat(response.getHoldername()).isNull();
        assertThat(response.getCardnumber()).isNull();
        assertThat(response.getCardType()).isNull();
        assertThat(response.getBank()).isEqualTo("Bank of Example");
        assertThat(response.getAccountnumber()).isEqualTo("123456789");
        assertThat(response.getRoutingnumber()).isEqualTo("012345678");
    }

    @Test
    void testGetAccountDetails_ForAdmin() {
        given(adminRepository.findById("admin@email.com")).willReturn(Optional.of(admin));

        AccountResponseDto response = accountService.getAccountDetails("admin@email.com");

        assertThat(response.getName()).isEqualTo("Admin Name");
        assertThat(response.getEmail()).isEqualTo("admin@email.com");
        assertThat(response.getPaymentType()).isNull();
        assertThat(response.getHoldername()).isNull();
        assertThat(response.getCardnumber()).isNull();
        assertThat(response.getCardType()).isNull();
        assertThat(response.getBank()).isNull();
        assertThat(response.getAccountnumber()).isNull();
        assertThat(response.getRoutingnumber()).isNull();
    }

    @Test
    void testUpdateAccount_ForClient() {
        given(clientRepository.findById("client@email.com")).willReturn(Optional.of(client));
        given(clientRepository.save(client)).willReturn(client);

        AccountUpdateDto updateDto = new AccountUpdateDto("New Name", "1111111111", "CREDIT_CARD");
        AccountResponseDto response = accountService.updateAccount("client@email.com", updateDto);

        assertThat(response.getName()).isEqualTo("New Name");
        assertThat(response.getPhone()).isEqualTo("1111111111");
        assertThat(response.getPaymentType()).isEqualTo("CREDIT_CARD");
        verify(creditCardRepository, times(1)).deleteById("client@email.com");
    }

    @Test
    void testUpdateAccount_ForAdmin() {
        given(adminRepository.findById("admin@email.com")).willReturn(Optional.of(admin));
        given(adminRepository.save(admin)).willReturn(admin);

        AccountUpdateDto updateDto = new AccountUpdateDto("New Admin Name", "2222222222", null);
        AccountResponseDto response = accountService.updateAccount("admin@email.com", updateDto);

        assertThat(response.getName()).isEqualTo("New Admin Name");
        assertThat(response.getPhone()).isEqualTo("2222222222");
        assertThat(response.getPaymentType()).isNull();
    }

    @Test
    void testUpdatePaymentDetails_ForCreditCard() {
        given(clientRepository.findById("client@email.com")).willReturn(Optional.of(client));

        PaymentDetailsDto dto = new PaymentDetailsDto("CREDIT_CARD", "Holder Name", "1234567890123456", "VISA", null, null, null);
        accountService.updatePaymentDetails("client@email.com", dto);

        verify(creditCardRepository, times(1)).save(any(CreditCard.class));
    }

    @Test
    void testUpdatePaymentDetails_ForBankAccount() {
        given(clientRepository.findById("client@email.com")).willReturn(Optional.of(client));

        PaymentDetailsDto dto = new PaymentDetailsDto("BANK_ACCOUNT", null, null, null, "Bank Name", "123456789", "012345678");
        accountService.updatePaymentDetails("client@email.com", dto);

        verify(bankAccountRepository, times(1)).save(any(BankAccount.class));
    }

    @Test
    void testDeleteAccount() {
        given(clientRepository.existsById("client@email.com")).willReturn(true);

        accountService.deleteAccount("client@email.com");

        verify(creditCardRepository, times(1)).deleteById("client@email.com");
        verify(bankAccountRepository, times(1)).deleteById("client@email.com");
        verify(clientRepository, times(1)).deleteById("client@email.com");
    }

    @Test
    void testDeleteAccount_NotFound() {
        given(clientRepository.existsById("nonexistent@email.com")).willReturn(false);

        assertThatThrownBy(() -> accountService.deleteAccount("nonexistent@email.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Account not found for email: nonexistent@email.com");
    }
}