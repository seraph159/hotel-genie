package com.jeff.hotel_management_system.service;

import com.jeff.hotel_management_system.dto.ClientResponseDto;
import com.jeff.hotel_management_system.entity.Client;
import com.jeff.hotel_management_system.repository.ClientRepository;
import com.jeff.hotel_management_system.utils.TokenWhitelistRedisCacheClient;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
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

        return clientRepository.save(client);
    }

    public Optional<Client> updateClient(String email, String phone) {

        this.tokenWhitelistRedisCacheClient.delete(email);
        return clientRepository.findById(email).map(existingClient -> {
            existingClient.setEmail(email);
            existingClient.setPhone(phone);
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
