package com.mts.aadati.controllers;

import com.mts.aadati.dto.request.LoginRequest;
import com.mts.aadati.security.CustomUserDetails;
import com.mts.aadati.utils.ApiResponse;
import com.mts.aadati.utils.AuthResponse;
import com.mts.aadati.dto.request.UserRequest;
import com.mts.aadati.services.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */

@RestController
@RequestMapping("/aadati/v1/auth")
@AllArgsConstructor
@Slf4j
@Validated
public class AuthController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody UserRequest request,
            HttpServletRequest httpRequest) {
        log.debug("POST /register called for email: {}", request.getEmail());

        try {
            AuthResponse authResponse = authenticationService.register(request);
            log.info("Registration successful for email: {}", request.getEmail());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("User registered successfully", authResponse));

        } catch (IllegalArgumentException e) {
            log.warn("Registration failed - bad request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));

        } catch (RuntimeException e) {
            log.error("Registration failed - internal error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Registration failed"));

        } catch (Exception e) {
            log.error("Unexpected error during registration: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Internal server error"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest) {
        log.debug("POST /login called for username: {}", loginRequest.getUsername());

        try {
            AuthResponse authResponse = authenticationService.login(
                    loginRequest.getUsername().trim(),
                    loginRequest.getPassword()
            );

            log.info("Login successful for username: {}", loginRequest.getUsername());
            return ResponseEntity.ok(ApiResponse.success("Login successful", authResponse));

        } catch (RuntimeException e) {
            log.warn("Login failed for username {}: {}", loginRequest.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid username or password"));

        } catch (Exception e) {
            log.error("Unexpected error during login for username {}: {}",
                    loginRequest.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Login failed"));
        }
    }
}
