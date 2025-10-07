package com.mts.aadati.controllers;

import com.mts.aadati.dto.request.UserRequest;
import com.mts.aadati.dto.response.UserResponse;
import com.mts.aadati.entities.Role;
import com.mts.aadati.entities.User;
import com.mts.aadati.security.CustomUserDetails;
import com.mts.aadati.services.AuthenticationService;
import com.mts.aadati.services.UserService;
import com.mts.aadati.utils.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */
@RestController
@RequestMapping("/aadati/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final AuthenticationService authenticationService;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    // ===== CRUD Operations =====

    @PutMapping("/update")
    public ResponseEntity<String> updateCurrentUser(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestBody @Valid UserRequest request) {
        logger.debug("PUT /update called for user: {}", currentUser.getUsername());

        try {
            Optional<User> result = userService.updateUser(currentUser.getUsername(), request);

            if (result.isPresent()) {
                logger.info("User updated successfully: {}", currentUser.getUsername());
                return ResponseEntity.ok("User updated successfully");
            } else {
                logger.warn("Failed to update user: {}", currentUser.getUsername());
                return ResponseEntity.badRequest().body("Failed to update user");
            }
        } catch (IllegalArgumentException e) {
            logger.warn("Bad request for update user: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Internal error while updating user: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Internal server error");
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(@AuthenticationPrincipal CustomUserDetails userDetails) {
        String username = userDetails.getUsername();
        logger.info("Delete account request for {}", username);

        authenticationService.deleteAccount(username);
        return ResponseEntity.ok(ApiResponse.success("Account deleted successfully", null));
    }


    @GetMapping("/all")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        logger.debug("GET /all called");

        try {
            List<UserResponse> users = userService.getAllUsers();
            logger.info("Retrieved {} users", users.size());
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Internal error while retrieving all users: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // ===== Current User Profile =====

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getCurrentUserProfile(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        logger.debug("GET /profile called for user: {}", currentUser.getUsername());

        try {
            Optional<UserResponse> user = userService.findByUsername(currentUser.getUsername());

            if (user.isPresent()) {
                logger.info("Retrieved profile for user: {}", currentUser.getUsername());
                return ResponseEntity.ok(user.get());
            } else {
                logger.warn("User profile not found: {}", currentUser.getUsername());
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Internal error while retrieving user profile: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/profile/roles")
    public ResponseEntity<List<String>> getCurrentUserRoles(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        logger.debug("GET /profile/roles called for user: {}", currentUser.getUsername());

        try {
            List<String> roles = userService.getRolesByUserId(currentUser.getId()).stream()
                    .map(Role::getName)
                    .toList();

            logger.info("Retrieved {} roles for user: {}", roles.size(), currentUser.getUsername());
            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            logger.error("Internal error while retrieving user roles: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // ===== Search Operations =====

    @GetMapping("/search/firstName/{firstName}")
    public ResponseEntity<List<UserResponse>> searchByFirstName(
            @NotBlank @Size(min = 1, max = 30)
            @Pattern(regexp = "^[\\p{L}\\p{M}\\s.'-]+$")
            @PathVariable String firstName) {
        logger.debug("GET /search/firstName/{} called", firstName);

        try {
            List<UserResponse> users = userService.findByFirstNameLike(firstName);
            logger.info("Found {} users with firstName like: {}", users.size(), firstName);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Internal error while searching by firstName: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/search/lastName/{lastName}")
    public ResponseEntity<List<UserResponse>> searchByLastName(
            @NotBlank @Size(min = 1, max = 50)
            @Pattern(regexp = "^[\\p{L}\\p{M}\\s.'-]+$")
            @PathVariable String lastName) {
        logger.debug("GET /search/lastName/{} called", lastName);

        try {
            List<UserResponse> users = userService.findByLastNameLike(lastName);
            logger.info("Found {} users with lastName like: {}", users.size(), lastName);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Internal error while searching by lastName: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/search/email/similar")
    public ResponseEntity<List<UserResponse>> searchBySimilarEmail(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        logger.debug("GET /search/email/similar called for user: {}", currentUser.getUsername());

        try {
            List<UserResponse> users = userService.findByEmailLike(currentUser.getEmail());
            logger.info("Found {} users with email like: {}", users.size(), currentUser.getEmail());
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Internal error while searching by email: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/search/username/{username}")
    public ResponseEntity<UserResponse> searchByUsername(
            @NotBlank @Size(min = 3, max = 30)
            @PathVariable String username) {
        logger.debug("GET /search/username/{} called", username);

        try {
            Optional<UserResponse> user = userService.findByUsername(username);

            if (user.isPresent()) {
                logger.info("Found user with username: {}", username);
                return ResponseEntity.ok(user.get());
            } else {
                logger.warn("User not found with username: {}", username);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Internal error while searching by username: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/search/fullName/{firstName}/{lastName}")
    public ResponseEntity<UserResponse> searchByFullName(
            @NotBlank @Size(min = 1, max = 30)
            @Pattern(regexp = "^[\\p{L}\\p{M}\\s.'-]+$")
            @PathVariable String firstName,
            @NotBlank @Size(min = 1, max = 50)
            @Pattern(regexp = "^[\\p{L}\\p{M}\\s.'-]+$")
            @PathVariable String lastName) {
        logger.debug("GET /search/fullName/{}/{} called", firstName, lastName);

        try {
            Optional<UserResponse> user = userService.findByFirstNameAndLastNameLike(firstName, lastName);

            if (user.isPresent()) {
                logger.info("Found user with fullName: {} {}", firstName, lastName);
                return ResponseEntity.ok(user.get());
            } else {
                logger.warn("User not found with fullName: {} {}", firstName, lastName);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Internal error while searching by fullName: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // ===== Date-based Search =====

    @GetMapping("/search/createdAt")
    public ResponseEntity<List<UserResponse>> searchByCreatedAtBetween(
            @RequestParam @NotNull Instant start,
            @RequestParam @NotNull Instant end) {
        logger.debug("GET /search/createdAt called with range: {} to {}", start, end);

        try {
            List<UserResponse> users = userService.findByCreatedAtBetween(start, end);
            logger.info("Found {} users created between {} and {}", users.size(), start, end);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Internal error while searching by createdAt: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/search/updatedAt")
    public ResponseEntity<List<UserResponse>> searchByUpdatedAtBetween(
            @RequestParam @NotNull Instant start,
            @RequestParam @NotNull Instant end) {
        logger.debug("GET /search/updatedAt called with range: {} to {}", start, end);

        try {
            List<UserResponse> users = userService.findByUpdatedAtBetween(start, end);
            logger.info("Found {} users updated between {} and {}", users.size(), start, end);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Internal error while searching by updatedAt: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // ===== Statistics and Reports =====

    @GetMapping("/recent")
    public ResponseEntity<List<UserResponse>> getLast10Users() {
        logger.debug("GET /recent called");

        try {
            List<UserResponse> users = userService.findLast10ByCreatedAtDesc();
            logger.info("getLast10Users Retrieved {} recent users", users.size());
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Internal error while retrieving recent users: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/email/verified")
    public ResponseEntity<List<UserResponse>> getEmailVerified() {
        logger.debug("GET /email/verified called");

        try {
            List<UserResponse> users = userService.findByEmailVerifiedTrue();
            logger.info("getEmailVerified Retrieved {} email verified users", users.size());
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Internal error while retrieving email verified users: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/email/unverified")
    public ResponseEntity<List<UserResponse>> getEmailUnverified() {
        logger.debug("GET /email/unverified called");

        try {
            List<UserResponse> users = userService.findByEmailVerifiedFalse();
            logger.info("getEmailUnverified Retrieved {} email unverified users", users.size());
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Internal error while retrieving email unverified users: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/email/count/verified")
    public ResponseEntity<Long> countEmailVerified() {
        logger.debug("GET /email/count/verified called");

        try {
            long count = userService.getCountByEmailVerifiedTrue();
            logger.info("Email verified users count: {}", count);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            logger.error("Internal error while counting email verified users: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/email/count/unverified")
    public ResponseEntity<Long> countEmailUnverified() {
        logger.debug("GET /email/count/unverified called");

        try {
            long count = userService.getCountByEmailVerifiedFalse();
            logger.info("Email unverified users count: {}", count);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            logger.error("Internal error while counting email unverified users: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/email/status/current")
    public ResponseEntity<String> getCurrentUserEmailStatus(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        logger.debug("GET /email/status/current called for user: {}", currentUser.getUsername());

        try {
            Optional<String> status = userService.getEmailIsActive(currentUser.getEmail());

            if (status.isPresent()) {
                logger.info("Email status found for user: {}", currentUser.getUsername());
                return ResponseEntity.ok(status.get());
            } else {
                logger.warn("Email status not found for user: {}", currentUser.getUsername());
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Internal error while checking email status: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // ===== Role Management =====

    @GetMapping("/role/{roleName}")
    public ResponseEntity<List<UserResponse>> getUsersByRole(
            @NotBlank @Size(min = 3, max = 50)
            @PathVariable String roleName) {
        logger.debug("GET /role/{} called", roleName);

        try {
            List<UserResponse> users = userService.getUsersByRoleName(roleName);
            logger.info("Found {} users with role: {}", users.size(), roleName);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Internal error while retrieving users by role: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/hasRole/{roleName}")
    public ResponseEntity<Boolean> checkCurrentUserRole(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @NotBlank @Size(min = 3, max = 50)
            @PathVariable String roleName) {
        logger.debug("GET /hasRole/{} called for user: {}", roleName, currentUser.getUsername());

        try {
            boolean hasRole = userService.checkIfUserHasRole(currentUser.getId(), roleName);
            logger.info("User {} has role {}: {}", currentUser.getUsername(), roleName, hasRole);
            return ResponseEntity.ok(hasRole);
        } catch (Exception e) {
            logger.error("Internal error while checking user role: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/withRoles")
    public ResponseEntity<List<UserResponse>> getAllUsersWithRoles() {
        logger.debug("GET /withRoles called");

        try {
            List<UserResponse> users = userService.getAllUsersWithRoles();
            logger.info("Retrieved {} users with roles", users.size());
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Internal error while retrieving users with roles: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/count/role/{roleName}")
    public ResponseEntity<Long> countUsersByRole(
            @NotBlank @Size(min = 3, max = 50)
            @PathVariable String roleName) {
        logger.debug("GET /count/role/{} called", roleName);

        try {
            long count = userService.countUsersWithRole(roleName);
            logger.info("Users with role {} count: {}", roleName, count);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            logger.error("Internal error while counting users by role: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}