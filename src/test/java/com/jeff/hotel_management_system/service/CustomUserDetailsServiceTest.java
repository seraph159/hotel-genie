package com.jeff.hotel_management_system.service;

import com.jeff.hotel_management_system.entity.Admin;
import com.jeff.hotel_management_system.entity.Client;
import com.jeff.hotel_management_system.entity.PaymentType;
import com.jeff.hotel_management_system.entity.User;
import com.jeff.hotel_management_system.repository.AdminRepository;
import com.jeff.hotel_management_system.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

class CustomUserDetailsServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private AdminRepository adminRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private Client client;
    private Admin admin;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        client = new Client("1234567890", PaymentType.CREDIT_CARD);
        client.setName("Client Name");
        client.setEmail("client@email.com");
        client.setPassword("password");
        client.setRole("CLIENT");
        admin = new Admin();
        admin.setName("Admin Name");
        admin.setEmail("admin@email.com");
        admin.setPassword("password");
        admin.setRole("ADMIN");
    }

    @Test
    void testLoadUserByUsername_WithClient() {
        given(clientRepository.findByEmail("client@email.com")).willReturn(Optional.of(client));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("client@email.com");

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("client@email.com");
        assertThat(userDetails.getAuthorities().stream().findFirst().get().getAuthority()).isEqualTo("ROLE_CLIENT");
    }

    @Test
    void testLoadUserByUsername_WithAdmin() {
        given(adminRepository.findByEmail("admin@email.com")).willReturn(Optional.of(admin));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("admin@email.com");

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("admin@email.com");
        assertThat(userDetails.getAuthorities().stream().findFirst().get().getAuthority()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        given(clientRepository.findByEmail("nonexistent@email.com")).willReturn(Optional.empty());
        given(adminRepository.findByEmail("nonexistent@email.com")).willReturn(Optional.empty());

        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername("nonexistent@email.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found with email: nonexistent@email.com");
    }
}