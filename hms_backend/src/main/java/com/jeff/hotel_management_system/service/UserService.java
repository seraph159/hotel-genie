package com.jeff.hotel_management_system.service;

import com.jeff.hotel_management_system.entity.Admin;
import com.jeff.hotel_management_system.entity.Client;
import com.jeff.hotel_management_system.exception.PasswordChangeIllegalArgumentException;
import com.jeff.hotel_management_system.repository.AdminRepository;
import com.jeff.hotel_management_system.repository.ClientRepository;
import com.jeff.hotel_management_system.utils.TokenWhitelistRedisCacheClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenWhitelistRedisCacheClient tokenWhitelistRedisCacheClient;

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

    public void changePassword(String email, String oldPassword, String newPassword, String confirmPassword) {

        // Validate new password and confirm password
        if (!newPassword.equals(confirmPassword)) {
            throw new PasswordChangeIllegalArgumentException("New password and confirmation password do not match.");
        }

        // Retrieve the user (Client or Admin) from the repository
        Optional<Client> clientOptional = clientRepository.findByEmail(email);
        Optional<Admin> adminOptional = adminRepository.findByEmail(email);

        if (clientOptional.isEmpty() && adminOptional.isEmpty()) {

            throw new UsernameNotFoundException("User not found with email: " + email);
        }


        if (clientOptional.isPresent()) {

            // Get the client
            Client client = clientOptional.get();

            // Verify the old password
            if (!passwordEncoder.matches(oldPassword, client.getPassword())) {
                throw new PasswordChangeIllegalArgumentException("Old password is incorrect.");
            }

            // Encode and update the password
            client.setPassword(passwordEncoder.encode(newPassword));

            //Remove from whitelist
            this.tokenWhitelistRedisCacheClient.delete(email);

            clientRepository.save(client);

        } else {

            Admin admin = adminOptional.get();

            if (!passwordEncoder.matches(oldPassword, admin.getPassword())) {
                throw new PasswordChangeIllegalArgumentException("Old password is incorrect.");
            }

            admin.setPassword(passwordEncoder.encode(newPassword));

            //Remove from whitelist
            this.tokenWhitelistRedisCacheClient.delete(email);

            adminRepository.save(admin);

        }

    }

}
