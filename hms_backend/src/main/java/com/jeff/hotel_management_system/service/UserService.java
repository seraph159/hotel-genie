package com.jeff.hotel_management_system.service;

import com.jeff.hotel_management_system.entity.Admin;
import com.jeff.hotel_management_system.entity.Client;
import com.jeff.hotel_management_system.repository.AdminRepository;
import com.jeff.hotel_management_system.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Registers a new Client.
     */
    public Client registerClient(Client client) {
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

    /**
     * Registers a new Admin.
     */
    public Admin registerAdmin(Admin admin) {
        // Check if the email is already registered
        if (adminRepository.existsByEmail(admin.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        admin.setRole("ADMIN"); // Role-based distinction

        // Save and return the registered admin
        return adminRepository.save(admin);
    }
}
