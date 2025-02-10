package com.jeff.hotel_management_system.controller;

import com.jeff.hotel_management_system.dto.AuthResponseDto;
import com.jeff.hotel_management_system.dto.LoginDto;
import com.jeff.hotel_management_system.dto.ValidationResponseDto;
import com.jeff.hotel_management_system.entity.Admin;
import com.jeff.hotel_management_system.entity.Client;
import com.jeff.hotel_management_system.utils.JwtUtils;
import com.jeff.hotel_management_system.service.AuthService;
import com.jeff.hotel_management_system.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication API", description = "Operations related to user authentication and registration")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/client/register")
    @Operation(
            summary = "Register a new client",
            description = "Registers a new client with the provided details",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Client registered successfully",
                            content = @Content(schema = @Schema(implementation = String.class))
                    )
            }
    )
    public ResponseEntity<?> registerClient(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Client details to register",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Client.class))
            )
            @RequestBody Client client
    ) {
        userService.registerClient(client); // Register the client
        return ResponseEntity.ok("Client registered successfully");
    }

    @PostMapping("/admin/register")
    @Operation(
            summary = "Register a new admin",
            description = "Registers a new admin with the provided details",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Admin registered successfully",
                            content = @Content(schema = @Schema(implementation = String.class))
                    )
            }
    )
    public ResponseEntity<?> registerAdmin(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Admin details to register",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Admin.class))
            )
            @RequestBody Admin admin
    ) {
        userService.registerAdmin(admin); // Register the admin
        return ResponseEntity.ok("Admin registered successfully");
    }

    @PostMapping("/login")
    @Operation(
            summary = "User login",
            description = "Authenticates a user (client or admin) and returns a JWT token and role",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Login successful",
                            content = @Content(schema = @Schema(implementation = AuthResponseDto.class))
                    )
            }
    )
    public ResponseEntity<?> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Login credentials",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoginDto.class))
            )
            @RequestBody LoginDto loginDto
    ) {
        // Authenticate the user and retrieve the token and role
        String[] tokenAndRole = authService.login(loginDto).split(",");
        String token = tokenAndRole[0];
        String role = tokenAndRole[1];

        // Prepare the response with the token and role
        AuthResponseDto authResponseDto = new AuthResponseDto();
        authResponseDto.setAccessToken(token);
        authResponseDto.setRole(role);

        return ResponseEntity.ok(authResponseDto);
    }

    @PostMapping("/validate-token")
    @Operation(
            summary = "Validate JWT token",
            description = "Validates the provided JWT token and returns the username if valid",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Token is valid",
                            content = @Content(schema = @Schema(implementation = ValidationResponseDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Missing or invalid Authorization header"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Invalid or expired token"
                    )
            }
    )
    public ResponseEntity<?> validateToken(
            @Parameter(
                    description = "Authorization header containing the JWT token",
                    required = true,
                    example = "Bearer <token>"
            )
            @RequestHeader("Authorization") String authHeader
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);

        if (jwtUtils.validateToken(token)) {
            String username = jwtUtils.getUsername(token);
            return ResponseEntity.ok().body(new ValidationResponseDto(true, username));
        } else {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }
    }

    @PatchMapping("/{email}/password")
    @Operation(
            summary = "Update client password",
            description = "Updates the password for a client with the provided email",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Password updated successfully",
                            content = @Content(schema = @Schema(implementation = String.class))
                    )
            }
    )
    public ResponseEntity<?> updateClientPassword(
            @Parameter(
                    description = "Email of the client whose password is to be updated",
                    required = true,
                    example = "client@example.com"
            )
            @PathVariable String email,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Password update details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
            @RequestBody Map<String, String> passwordObject
    ) {
        String oldPassword = passwordObject.get("oldPassword");
        String newPassword = passwordObject.get("newPassword");
        String confirmPassword = passwordObject.get("confirmPassword");
        userService.changePassword(email, oldPassword, newPassword, confirmPassword);
        return ResponseEntity.ok("Password changed successfully!");
    }
}