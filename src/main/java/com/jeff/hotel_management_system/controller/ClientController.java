package com.jeff.hotel_management_system.controller;

import com.jeff.hotel_management_system.dto.ClientResponseDto;
import com.jeff.hotel_management_system.entity.Client;
import com.jeff.hotel_management_system.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/api/clients")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Client API", description = "Operations related to client management (Admin only)")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @GetMapping
    @Operation(
            summary = "Get all clients",
            description = "Retrieves a list of all clients. Requires ADMIN role.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of clients retrieved successfully",
                            content = @Content(schema = @Schema(implementation = ClientResponseDto.class))
                    )
            }
    )
    public List<ClientResponseDto> getAllClients() {
        return clientService.getAllClients();
    }

    @GetMapping("/{email}")
    @Operation(
            summary = "Get client by email",
            description = "Retrieves a client by their email. Requires ADMIN role.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Client retrieved successfully",
                            content = @Content(schema = @Schema(implementation = Client.class))
                    )
            }
    )
    public ResponseEntity<Client> getClientByEmail(
            @Parameter(description = "Email of the client", required = true, example = "client@example.com")
            @PathVariable String email
    ) {
        return clientService.getClientByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(
            summary = "Create a new client",
            description = "Creates a new client. Requires ADMIN role.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Client created successfully",
                            content = @Content(schema = @Schema(implementation = Client.class))
                    )
            }
    )
    public Client createClient(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Client details to create",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Client.class))
            )
            @RequestBody Client client
    ) {
        return clientService.createClient(client);
    }

    @PutMapping("/{email}/{phone}")
    @Operation(
            summary = "Update client phone number",
            description = "Updates the phone number of a client by their email. Requires ADMIN role.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Client updated successfully",
                            content = @Content(schema = @Schema(implementation = Client.class))
                    )
            }
    )
    public ResponseEntity<Client> updateClient(
            @Parameter(description = "Email of the client", required = true, example = "client@example.com")
            @PathVariable String email,

            @Parameter(description = "New phone number of the client", required = true, example = "1234567890")
            @PathVariable String phone
    ) {
        return clientService.updateClient(email, phone)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{email}")
    @Operation(
            summary = "Delete a client",
            description = "Deletes a client by their email. Requires ADMIN role.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Client deleted successfully"
                    )
            }
    )
    public ResponseEntity<Void> deleteClient(
            @Parameter(description = "Email of the client", required = true, example = "client@example.com")
            @PathVariable String email
    ) {
        boolean deleted = clientService.deleteClient(email);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}