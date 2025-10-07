package com.mts.aadati.services;


import com.mts.aadati.entities.User;
import com.mts.aadati.security.CustomUserDetails;
import com.mts.aadati.utils.AuthResponse;
import com.mts.aadati.dto.request.UserRequest;
import com.mts.aadati.security.jwt.JwtService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */
@Component
@AllArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse register(UserRequest request) throws RuntimeException {
        log.debug("register called for email: {}", request.getEmail());

        try {
            Optional<User> savedUser = userService.addUserEntity(request);

            if (savedUser.isEmpty()) {
                log.error("Failed to save user during registration");
                throw new AuthenticationServiceException("User could not be saved");
            }

            User user = savedUser.get();

            List<GrantedAuthority> authorities = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName()))
                    .map(GrantedAuthority.class::cast)
                    .toList();

            CustomUserDetails userDetails = new CustomUserDetails(
                    user.getUserId(),
                    user.getUsername(),
                    user.getPassword(),
                    user.getEmail(),
                    authorities
            );

            String accessToken = jwtService.generateAccessToken(userDetails);

            log.info("User registered successfully: {}", user.getUsername());

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(null)
                    .expiresIn(System.currentTimeMillis() + 3600000)
                    .build();

        } catch (IllegalArgumentException e) {
            log.warn("Registration failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during registration: {}", e.getMessage());
            throw new AuthenticationServiceException("Registration failed due to internal error");
        }
    }

    public AuthResponse login(String username, String password) {
        log.debug("login called for username: {}", username);

        try {
            Optional<User> userOpt = userService.findByUsernameEntity(username);

            if (userOpt.isEmpty()) {
                log.warn("Login failed: User not found with username: {}", username);
                throw new AuthenticationServiceException("Invalid username or password");
            }

            User user = userOpt.get();

            if (!passwordEncoder.matches(password, user.getPassword())) {
                log.warn("Login failed: Invalid password for username: {}", username);
                throw new AuthenticationServiceException("Invalid username or password");
            }

            List<GrantedAuthority> authorities = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName()))
                    .map(GrantedAuthority.class::cast)
                    .toList();

            CustomUserDetails userDetails = new CustomUserDetails(
                    user.getUserId(),
                    user.getUsername(),
                    user.getPassword(),
                    user.getEmail(),
                    authorities
            );

            String accessToken = jwtService.generateAccessToken(userDetails);
            log.info("User logged in successfully: {}", username);

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(null)
                    .expiresIn(System.currentTimeMillis() + 3600000)
                    .build();

        } catch (RuntimeException e) {
            log.warn("Login failed for username {}: {}", username, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during login for username {}: {}", username, e.getMessage());
            throw new AuthenticationServiceException("Login failed due to internal error");
        }
    }



    public void deleteAccount(String token) {
        String username = jwtService.extractUsername(token);
        Optional<User> userOpt = userService.findByUsernameEntity(username);

        if (userOpt.isPresent()) {
            userService.removeUserById(userOpt.get().getUserId());
        } else {
            throw new AuthenticationServiceException("User not found");
        }
    }

}
