package com.jeff.hotel_management_system.service;

import com.jeff.hotel_management_system.entity.Client;
import com.jeff.hotel_management_system.entity.User;
import com.jeff.hotel_management_system.repository.AdminRepository;
import com.jeff.hotel_management_system.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = findByEmail(email);
        return user.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    private Optional<User> findByEmail(String email) {
        Optional<Client> client = clientRepository.findByEmail(email);
        if (client.isPresent()) {
            return Optional.of(client.get());
        }
        return adminRepository.findByEmail(email).map(admin -> (User) admin);
    }
}