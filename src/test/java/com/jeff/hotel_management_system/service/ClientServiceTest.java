package com.jeff.hotel_management_system.service;

import com.jeff.hotel_management_system.dto.ClientResponseDto;
import com.jeff.hotel_management_system.entity.Client;
import com.jeff.hotel_management_system.entity.PaymentType;
import com.jeff.hotel_management_system.repository.ClientRepository;
import com.jeff.hotel_management_system.utils.TokenWhitelistRedisCacheClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private TokenWhitelistRedisCacheClient tokenWhitelistRedisCacheClient;

    @InjectMocks
    private ClientService clientService;

    private Client client;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        client = new Client("1234567890", PaymentType.CREDIT_CARD);
        client.setName("John Doe");
        client.setEmail("client@email.com");
        client.setPassword("password");
        client.setRole("CLIENT");
    }

    @Test
    void testGetAllClients() {
        Client client2 = new Client("0987654321", PaymentType.BANK_ACCOUNT);
        client2.setName("Jane Doe");
        client2.setEmail("client2@email.com");
        client2.setPassword("password");
        client2.setRole("CLIENT");
        given(clientRepository.findAll()).willReturn(Arrays.asList(client, client2));

        List<ClientResponseDto> clients = clientService.getAllClients();

        assertThat(clients).hasSize(2);
        assertThat(clients.get(0).getName()).isEqualTo("John Doe");
        assertThat(clients.get(1).getName()).isEqualTo("Jane Doe");
        verify(clientRepository, times(1)).findAll();
    }

    @Test
    void testGetClientByEmail() {
        given(clientRepository.findById("client@email.com")).willReturn(Optional.of(client));

        Optional<Client> result = clientService.getClientByEmail("client@email.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("client@email.com");
        verify(clientRepository, times(1)).findById("client@email.com");
    }

    @Test
    void testCreateClient() {
        given(clientRepository.save(client)).willReturn(client);

        Client createdClient = clientService.createClient(client);

        assertThat(createdClient).isNotNull();
        assertThat(createdClient.getEmail()).isEqualTo("client@email.com");
        verify(clientRepository, times(1)).save(client);
    }

    @Test
    void testUpdateClient() {
        given(clientRepository.findById("client@email.com")).willReturn(Optional.of(client));
        given(clientRepository.save(client)).willReturn(client);

        Optional<Client> result = clientService.updateClient("client@email.com", "0123456789");

        assertThat(result).isPresent();
        assertThat(result.get().getPhone()).isEqualTo("0123456789");
        verify(tokenWhitelistRedisCacheClient, times(1)).delete("client@email.com");
        verify(clientRepository, times(1)).findById("client@email.com");
        verify(clientRepository, times(1)).save(client);
    }

    @Test
    void testDeleteClient() {
        given(clientRepository.findById("client@email.com")).willReturn(Optional.of(client));

        boolean result = clientService.deleteClient("client@email.com");

        assertThat(result).isTrue();
        verify(tokenWhitelistRedisCacheClient, times(1)).delete("client@email.com");
        verify(clientRepository, times(1)).findById("client@email.com");
        verify(clientRepository, times(1)).delete(client);
    }

    @Test
    void testDeleteClient_NotFound() {
        given(clientRepository.findById("nonexistent@email.com")).willReturn(Optional.empty());

        boolean result = clientService.deleteClient("nonexistent@email.com");

        assertThat(result).isFalse();
        verify(tokenWhitelistRedisCacheClient, times(1)).delete("nonexistent@email.com");
        verify(clientRepository, times(1)).findById("nonexistent@email.com");
        verify(clientRepository, never()).delete(any());
    }
}