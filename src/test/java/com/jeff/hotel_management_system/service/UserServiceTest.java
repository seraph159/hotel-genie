package com.jeff.hotel_management_system.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.jeff.hotel_management_system.entity.Admin;
import com.jeff.hotel_management_system.entity.Client;
import com.jeff.hotel_management_system.exception.PasswordChangeIllegalArgumentException;
import com.jeff.hotel_management_system.repository.AdminRepository;
import com.jeff.hotel_management_system.repository.ClientRepository;
import com.jeff.hotel_management_system.utils.TokenWhitelistRedisCacheClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenWhitelistRedisCacheClient tokenWhitelistRedisCacheClient;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        // MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterClient() {
        Client client = new Client();
        client.setEmail("client@example.com");
        client.setPassword("password");

        given(clientRepository.existsByEmail(client.getEmail())).willReturn(false);
        given(passwordEncoder.encode(client.getPassword())).willReturn("encodedPassword");
        given(clientRepository.save(client)).willReturn(client);

        Client result = userService.registerClient(client);

        assertThat(result.getRole()).isEqualTo("CLIENT");
        assertThat(result.getPassword()).isEqualTo("encodedPassword");
        verify(clientRepository, times(1)).save(client);
    }

    @Test
    void testRegisterClient_EmailAlreadyExists() {
        Client client = new Client();
        client.setEmail("client@example.com");

        given(clientRepository.existsByEmail(client.getEmail())).willReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.registerClient(client));

        assertThat(exception.getMessage()).isEqualTo("Email already registered");
        verify(clientRepository, never()).save(any());
    }

    @Test
    void testRegisterAdmin() {
        Admin admin = new Admin();
        admin.setEmail("admin@example.com");
        admin.setPassword("password");

        given(adminRepository.existsByEmail(admin.getEmail())).willReturn(false);
        given(passwordEncoder.encode(admin.getPassword())).willReturn("encodedPassword");
        given(adminRepository.save(admin)).willReturn(admin);

        Admin result = userService.registerAdmin(admin);

        assertThat(result.getRole()).isEqualTo("ADMIN");
        assertThat(result.getPassword()).isEqualTo("encodedPassword");
        verify(adminRepository, times(1)).save(admin);
    }

    @Test
    void testRegisterAdmin_EmailAlreadyExists() {
        Admin admin = new Admin();
        admin.setEmail("admin@example.com");

        given(adminRepository.existsByEmail(admin.getEmail())).willReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.registerAdmin(admin));

        assertThat(exception.getMessage()).isEqualTo("Email already registered");
        verify(adminRepository, never()).save(any());
    }

    @Test
    void testChangePassword_ClientSuccess() {
        String email = "client@example.com";
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";
        String confirmPassword = "newPassword";

        Client client = new Client();
        client.setEmail(email);
        client.setPassword("encodedOldPassword");

        given(clientRepository.findByEmail(email)).willReturn(Optional.of(client));
        given(passwordEncoder.matches(oldPassword, client.getPassword())).willReturn(true);
        given(passwordEncoder.encode(newPassword)).willReturn("encodedNewPassword");

        userService.changePassword(email, oldPassword, newPassword, confirmPassword);

        assertThat(client.getPassword()).isEqualTo("encodedNewPassword");
        verify(tokenWhitelistRedisCacheClient, times(1)).delete(email);
        verify(clientRepository, times(1)).save(client);
    }

    @Test
    void testChangePassword_AdminSuccess() {
        String email = "admin@example.com";
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";
        String confirmPassword = "newPassword";

        Admin admin = new Admin();
        admin.setEmail(email);
        admin.setPassword("encodedOldPassword");

        given(adminRepository.findByEmail(email)).willReturn(Optional.of(admin));
        given(passwordEncoder.matches(oldPassword, admin.getPassword())).willReturn(true);
        given(passwordEncoder.encode(newPassword)).willReturn("encodedNewPassword");

        userService.changePassword(email, oldPassword, newPassword, confirmPassword);

        assertThat(admin.getPassword()).isEqualTo("encodedNewPassword");
        verify(tokenWhitelistRedisCacheClient, times(1)).delete(email);
        verify(adminRepository, times(1)).save(admin);
    }

    @Test
    void testChangePassword_UserNotFound() {
        String email = "user@example.com";
        given(clientRepository.findByEmail(email)).willReturn(Optional.empty());
        given(adminRepository.findByEmail(email)).willReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () ->
                userService.changePassword(email, "oldPassword", "newPassword", "newPassword"));

        assertThat(exception.getMessage()).isEqualTo("User not found with email: " + email);
    }

    @Test
    void testChangePassword_InvalidOldPassword() {
        String email = "client@example.com";
        String oldPassword = "wrongOldPassword";
        String newPassword = "newPassword";
        String confirmPassword = "newPassword";

        Client client = new Client();
        client.setEmail(email);
        client.setPassword("encodedOldPassword");

        given(clientRepository.findByEmail(email)).willReturn(Optional.of(client));
        given(passwordEncoder.matches(oldPassword, client.getPassword())).willReturn(false);

        PasswordChangeIllegalArgumentException exception = assertThrows(PasswordChangeIllegalArgumentException.class, () ->
                userService.changePassword(email, oldPassword, newPassword, confirmPassword));

        assertThat(exception.getMessage()).isEqualTo("Old password is incorrect.");
    }

    @Test
    void testChangePassword_PasswordMismatch() {
        String email = "client@example.com";
        String oldPassword = "oldPassword";
        String newPassword = "newPassword1";
        String confirmPassword = "newPassword2";

        PasswordChangeIllegalArgumentException exception = assertThrows(PasswordChangeIllegalArgumentException.class, () ->
                userService.changePassword(email, oldPassword, newPassword, confirmPassword));

        assertThat(exception.getMessage()).isEqualTo("New password and confirmation password do not match.");
    }
}