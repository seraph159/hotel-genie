package com.jeff.hotel_management_system.controller;

import com.jeff.hotel_management_system.dto.ClientResponseDto;
import com.jeff.hotel_management_system.entity.Client;
import com.jeff.hotel_management_system.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clients")
@PreAuthorize("hasRole('ADMIN')")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @GetMapping
    public List<ClientResponseDto> getAllClients() {

        return clientService.getAllClients();
    }

    @GetMapping("/{email}")
    public ResponseEntity<Client> getClientByEmail(@PathVariable String email) {

        return clientService.getClientByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Client createClient(@RequestBody Client client) {

        return clientService.createClient(client);
    }

    @PutMapping("/{email}/{phone}")
    public ResponseEntity<Client> updateClient(@PathVariable String email, @PathVariable String phone) {

        return clientService.updateClient(email, phone)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteClient(@PathVariable String email) {

        boolean deleted = clientService.deleteClient(email);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }


}