package com.mts.aadati.services;

import com.mts.aadati.entities.Role;
import com.mts.aadati.entities.User;
import com.mts.aadati.dto.mapper.UserMapper;
import com.mts.aadati.repository.UserRepository;
import com.mts.aadati.dto.request.UserRequest;
import com.mts.aadati.dto.response.UserResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */
@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper ;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    // ===== Helper Methods CRUD =====
    @Transactional
    public Optional<User> addUser(User user) {
        logger.debug("addUser called with: {}", user != null ? user.getUsername() : "null");
        if (user == null) {
            logger.warn("addUser failed: user is null");
            return Optional.empty();
        }

        if (user.getUsername() != null && userRepository.findByUsername(user.getUsername()).isPresent()) {
            logger.warn("addUser failed: Username {} already exists", user.getUsername());
            return Optional.empty();
        }
        if (user.getEmail() != null && userRepository.findByEmail(user.getEmail()).isPresent()) {
            logger.warn("addUser failed: Email {} already exists", user.getEmail());
            return Optional.empty();
        }

        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        User saved = userRepository.save(user);
        logger.info("User added successfully with username: {}", saved.getUsername());
        return Optional.of(saved);
    }

    @Transactional
    public Optional<User> addUserEntity(UserRequest userRequest) {
        logger.debug("addUserEntity called with: {}", userRequest != null ? userRequest.getUsername() : "null");
        if (userRequest == null) {
            logger.warn("addUserEntity failed: userRequest is null");
            return Optional.empty();
        }

        if (userRequest.getUsername() != null
                && userRepository.findByUsername(userRequest.getUsername()).isPresent()) {
            logger.warn("addUserEntity failed: Username {} already exists", userRequest.getUsername());
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRequest.getEmail() != null && userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
            logger.warn("addUserEntity failed: Email {} already exists", userRequest.getEmail());
            throw new IllegalArgumentException("Email already exists");
        }

        User user = userMapper.toEntity(userRequest);

        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        User saved = userRepository.save(user);
        logger.info("User entity added successfully with username: {}", saved.getUsername());
        return Optional.of(saved);
    }

    @Transactional
    public Optional<User> updateUser(String usernameOrEmail, UserRequest userRequest) {
        logger.debug("updateUser called with: {}", usernameOrEmail);
        if (userRequest == null) {
            logger.warn("updateUser failed: userRequest is null");
            return Optional.empty();
        }

        Optional<User> existingUser = userRepository.findByUsername(usernameOrEmail);
        if (existingUser.isEmpty()) {
            existingUser = userRepository.findByEmail(usernameOrEmail);
        }

        if (existingUser.isEmpty()) {
            logger.warn("updateUser failed: User not found with: {}", usernameOrEmail);
            throw new NoSuchElementException("User not found");
        }

        User existing = existingUser.get();

        if (userRequest.getFirstName() != null) existing.setFirstName(userRequest.getFirstName());
        if (userRequest.getLastName() != null) existing.setLastName(userRequest.getLastName());
        if (userRequest.getPassword() != null && !userRequest.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        }

        if (userRequest.getEmail() != null && !userRequest.getEmail().equals(existing.getEmail())) {
            if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
                logger.warn("updateUser failed: Email {} already exists", userRequest.getEmail());
                throw new IllegalArgumentException("Email already exists");
            }
            existing.setEmail(userRequest.getEmail());
        }

        User saved = userRepository.save(existing);
        logger.info("User updated successfully: {}", saved.getUsername());
        return Optional.of(saved);
    }

    public boolean removeUserById(UUID uuid) {
        logger.debug("removeUserById called with: {}", uuid);
        if (uuid == null) {
            logger.warn("removeUserById failed: uuid is null");
            return false;
        }
        if (!userRepository.existsById(uuid)) {
            logger.warn("removeUserById failed: User not found with id: {}", uuid);
            return false;
        }
        userRepository.deleteById(uuid);
        logger.info("User deleted successfully with id: {}", uuid);
        return true;
    }

    public Optional<UserResponse> checkPassword(String username, String rawPassword) {
        logger.debug("checkPassword called with username: {}", username);
        if (username == null || rawPassword == null) {
            logger.warn("checkPassword failed: username or password is null");
            return Optional.empty();
        }

        return userRepository.findByUsername(username)
                .filter(user -> user.getPassword() != null && passwordEncoder.matches(rawPassword, user.getPassword()))
                .map(user -> {
                    logger.info("Password check successful for username: {}", username);
                    return convertToUserResponse(user);
                });
    }

    public List<UserResponse> getAllUsers() {
        return Optional.of(userRepository.findAll())
                .orElse(Collections.emptyList())
                .stream()
                .map(this::convertToUserResponse)
                .toList();
    }

    public List<User> getEntityAllUsers() {
        return Optional.of(userRepository.findAll())
                .orElse(Collections.emptyList());
    }

    public Optional<User> findByUsernameEntity(String username){
        logger.debug("findByUsernameEntity called with: {}", username);
        if (username.isBlank()) {
            logger.warn("findByUsernameEntity failed: username is blank");
            return Optional.empty();
        }
        logger.info("findByUsernameEntity Found user with username: {}",username);
        return userRepository.findByUsername(username) ;
    }

    public Optional<UserResponse> findByUsername(String username) {
        logger.debug("findByUsername called with: {}", username);
        if (username.isBlank()) {
            logger.warn("findByUsername failed: username is blank");
            return Optional.empty();
        }
        logger.info("Found users with username: {}", username);
        return userRepository.findByUsername(username)
                .map(this::convertToUserResponse);
    }

    public List<UserResponse> findByFirstNameLike(String firstName) {
        if (firstName.isBlank())
            return Collections.emptyList();
        return userRepository.findByFirstNameContainingIgnoreCase(firstName)
                .stream()
                .map(this::convertToUserResponse)
                .toList();
    }

    public List<UserResponse> findByLastNameLike(String lastName) {
        if (lastName.isBlank())
            return Collections.emptyList();
        return userRepository.findByLastNameContainingIgnoreCase(lastName)
                .stream()
                .map(this::convertToUserResponse)
                .toList();
    }

    public List<UserResponse> findByEmailLike(String email) {
        if (email.isBlank())
            return Collections.emptyList();
        return userRepository.findByEmailContainingIgnoreCase(email)
                .stream()
                .map(this::convertToUserResponse)
                .toList();
    }

    public Optional<UserResponse> findByFirstNameAndLastNameLike(String firstName, String lastName) {
        boolean bFirstName = !(firstName.isBlank());
        boolean bLastName = !(lastName.isBlank());

        return (bLastName && bFirstName) ?
                userRepository.findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(firstName, lastName)
                        .map(this::convertToUserResponse) :
                Optional.empty();
    }

    // ===== Helper Methods Search By Date =====
    public List<UserResponse> findByUpdatedAt(Instant updatedAt) {
        if (updatedAt == null)
            return Collections.emptyList();
        return userRepository.findByUpdatedAt(updatedAt)
                .stream()
                .map(this::convertToUserResponse)
                .toList();
    }

    public List<UserResponse> findByCreatedAt(Instant createdAt) {
        if (createdAt == null)
            return Collections.emptyList();
        return userRepository.findByCreatedAt(createdAt)
                .stream()
                .map(this::convertToUserResponse)
                .toList();
    }

    public List<UserResponse> findByUpdatedAtBetween(Instant start, Instant end) {
        if (start == null || end == null)
            return Collections.emptyList();
        return userRepository.findByUpdatedAtBetween(start, end)
                .stream()
                .map(this::convertToUserResponse)
                .toList();
    }

    public List<UserResponse> findByCreatedAtBetween(Instant start, Instant end) {
        if (start == null || end == null)
            return Collections.emptyList();
        return userRepository.findByCreatedAtBetween(start, end)
                .stream()
                .map(this::convertToUserResponse)
                .toList();
    }

    // ===== Helper Method Get Last 10 Users =====
    public List<UserResponse> findLast10ByCreatedAtDesc() {
        return Optional.ofNullable(userRepository.findTop10ByOrderByCreatedAtDesc())
                .orElse(Collections.emptyList())
                .stream()
                .map(this::convertToUserResponse)
                .toList();
    }

    // ===== Helper Method For Email Verified =====
    public long getCountByEmailVerifiedTrue() {
        return userRepository.countByEmailVerifiedTrue();
    }

    public long getCountByEmailVerifiedFalse() {
        return userRepository.countByEmailVerifiedFalse();
    }

    public Optional<String> getEmailIsActive(String email) {
        return (email != null && !email.isBlank()) ?
                userRepository.getEmailIsActive(email) :
                Optional.empty();
    }

    public List<UserResponse> findByEmailVerifiedFalse() {
        return Optional.ofNullable(userRepository.findByEmailVerifiedFalse())
                .orElse(Collections.emptyList())
                .stream()
                .map(this::convertToUserResponse)
                .toList();
    }

    public List<UserResponse> findByEmailVerifiedTrue() {
        return Optional.ofNullable(userRepository.findByEmailVerifiedTrue())
                .orElse(Collections.emptyList())
                .stream()
                .map(this::convertToUserResponse)
                .toList();
    }

    // ===== Helper Methods For Roles =====
    public List<Role> getRolesByUserId(UUID userId) {
        logger.debug("getRolesByUserId called with: {}", userId);
        if (userId == null) {
            logger.warn("getRolesByUserId failed: userId is null");
            return Collections.emptyList();
        }
        List<Role> roles = userRepository.findRolesByUserId(userId);
        logger.info("Found {} roles for userId {}", roles.size(), userId);
        return roles;
    }

    public List<UserResponse> getUsersByRoleName(String roleName) {
        logger.debug("getUsersByRoleName called with: {}", roleName);
        if (roleName == null || roleName.isBlank()) {
            logger.warn("getUsersByRoleName failed: roleName is blank");
            return Collections.emptyList();
        }
        List<User> users = userRepository.findUsersByRoleName(roleName);
        logger.info("Found {} users with role: {}", users.size(), roleName);
        return users.stream()
                .map(this::convertToUserResponse)
                .toList();
    }

    public boolean checkIfUserHasRole(UUID userId, String roleName) {
        logger.debug("checkIfUserHasRole called with userId: {}, roleName: {}", userId, roleName);
        if (userId == null || roleName == null || roleName.isBlank()) {
            logger.warn("checkIfUserHasRole failed: userId or roleName is invalid");
            return false;
        }
        boolean hasRole = userRepository.userHasRole(userId, roleName);
        logger.info("User {} has role {} ? {}", userId, roleName, hasRole);
        return hasRole;
    }

    public List<UserResponse> getAllUsersWithRoles() {
        logger.info("getAllUsersWithRoles called");
        List<User> users = userRepository.findAllWithRoles();
        logger.info("Fetched {} users with roles", users.size());
        return users.stream()
                .map(this::convertToUserResponse)
                .toList();
    }

    public long countUsersWithRole(String roleName) {
        logger.debug("countUsersWithRole called with: {}", roleName);
        if (roleName == null || roleName.isBlank()) {
            logger.warn("countUsersWithRole failed: roleName is blank");
            return 0;
        }
        long count = userRepository.countUsersByRole(roleName);
        logger.info("Number of users with role {}: {}", roleName, count);
        return count;
    }

    // ===== Helper Method to Convert User to UserResponse with Roles =====
    private UserResponse convertToUserResponse(User user) {
        UserResponse userResponse = UserMapper.toResponse(user);

        List<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .toList();
        userResponse.setRoles(roleNames);

        return userResponse;
    }
}