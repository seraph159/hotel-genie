package com.jeff.hotel_management_system.service;

import com.jeff.hotel_management_system.dto.ClientResponseDto;
import com.jeff.hotel_management_system.entity.Client;
import com.jeff.hotel_management_system.repository.AdminRepository;
import com.jeff.hotel_management_system.repository.ClientRepository;
import com.jeff.hotel_management_system.utils.TokenWhitelistRedisCacheClient;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private TokenWhitelistRedisCacheClient tokenWhitelistRedisCacheClient;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AdminRepository adminRepository;

    public List<ClientResponseDto> getAllClients() {

        return clientRepository.findAll().stream()
                .map(client -> new ClientResponseDto(
                        client.getName(),
                        client.getEmail(),
                        client.getPhone(),
                        client.getPaymentType() != null ? client.getPaymentType().name() : null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                ))
                .collect(Collectors.toList());
    }

    public Optional<Client> getClientByEmail(String email) {

        return clientRepository.findById(email);
    }

    public Client createClient(Client client) {

        // Check if email exists in admin table
        if (adminRepository.existsByEmail(client.getEmail())) {
            throw new IllegalArgumentException("Email is already in use by an admin");
        }

        // Check if the email is already registered
        if (clientRepository.existsByEmail(client.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // Populate and encode fields
        client.setPassword(passwordEncoder.encode(client.getPassword()));
        client.setRole("CLIENT"); // Role-based distinction

        // Save and return the registered client
        return clientRepository.save(client);
    }

    public Optional<Client> updateClient(String email, String phone) {

        // Check if email exists in admin table
        if (adminRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email is already in use by an admin");
        }

        this.tokenWhitelistRedisCacheClient.delete(email);
        return clientRepository.findById(email).map(existingClient -> {
            existingClient.setEmail(email);
            existingClient.setPhone(phone);
            return clientRepository.save(existingClient);
        });
    }

    public Optional<Client> updateClient(String email, Client updatedClient) {

        // Check if email exists in admin table
        if (adminRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email is already in use by an admin");
        }

        this.tokenWhitelistRedisCacheClient.delete(email);
        return clientRepository.findById(email).map(existingClient -> {
            existingClient.setName(updatedClient.getName());
            existingClient.setPhone(updatedClient.getPhone());
            existingClient.setPaymentType(updatedClient.getPaymentType());
            // Don't update email (it's the identifier) or password
            return clientRepository.save(existingClient);
        });
    }

    public boolean deleteClient(String email) {

        this.tokenWhitelistRedisCacheClient.delete(email);
        return clientRepository.findById(email).map(client -> {
            clientRepository.delete(client);
            return true;
        }).orElse(false);
    }
}
