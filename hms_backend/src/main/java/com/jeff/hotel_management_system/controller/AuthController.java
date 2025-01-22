package com.jeff.hotel_management_system.controller;

import com.jeff.hotel_management_system.dto.AuthResponseDto;
import com.jeff.hotel_management_system.dto.LoginDto;
import com.jeff.hotel_management_system.dto.ValidationResponseDto;
import com.jeff.hotel_management_system.entity.Admin;
import com.jeff.hotel_management_system.entity.Client;
import com.jeff.hotel_management_system.utils.JwtUtils;
import com.jeff.hotel_management_system.service.AuthService;
import com.jeff.hotel_management_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * Register a new Client
     */
    @PostMapping("/client/register")
    public ResponseEntity<?> registerClient(@RequestBody Client client) {

        userService.registerClient(client); // Register the client
        return ResponseEntity.ok("Client registered successfully");
    }

    /**
     * Register a new Admin
     */
    @PostMapping("/admin/register")
    public ResponseEntity<?> registerAdmin(@RequestBody Admin admin) {

        userService.registerAdmin(admin); // Register the admin
        return ResponseEntity.ok("Admin registered successfully");
    }

    /**
     * User Login (Client or Admin)
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {

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
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {

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
    public ResponseEntity<String> updateClientPassword(@PathVariable String email, Map<String, String> passwordObject) {
        String oldPassword = passwordObject.get("oldPassword");
        String newPassword = passwordObject.get("newPassword");
        String confirmPassword = passwordObject.get("confirmPassword");
        userService.changePassword(email, oldPassword, newPassword, confirmPassword);
        return ResponseEntity.ok("Password Changes Successfully!");

    }


}