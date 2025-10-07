package com.mts.aadati.repository;

import com.mts.aadati.entities.Role;
import com.mts.aadati.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    // ===== Helper Fields Search By Like =====
    List<User> findByFirstNameContainingIgnoreCase(String firstName);
    List<User> findByLastNameContainingIgnoreCase(String lastName);
    List<User> findByEmailContainingIgnoreCase(String email);
    Optional<User> findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(String firstName, String lastName);
    Optional <User> findByUsername(String username);
    Optional <User> findByEmail (String email) ;

    // ===== Helper Fields Search By Date =====
    List<User> findByUpdatedAt(Instant updatedAt);
    List<User> findByCreatedAt(Instant createdAt);
    List<User> findByUpdatedAtBetween(Instant start, Instant end);
    List<User> findByCreatedAtBetween(Instant start, Instant end);

    // Last 10 users
    List<User> findTop10ByOrderByCreatedAtDesc();

    // Custom queries
    //@Query("SELECT COUNT(u) FROM User u WHERE u.emailVerified = true")
    long countByEmailVerifiedTrue();
    //@Query("SELECT COUNT(u) FROM User u WHERE u.emailVerified = false")
    long countByEmailVerifiedFalse();

    @Query("SELECT u.email FROM User u WHERE u.email = :email AND u.emailVerified = true")
    Optional<String> getEmailIsActive(@Param("email") String email);

    List<User> findByEmailVerifiedFalse();
    List<User> findByEmailVerifiedTrue();

    // === NEW QUERIES FOR ROLES ===

    // Get roles for a specific user
    @Query("SELECT r FROM Role r JOIN r.users u WHERE u.userId = :userId AND r.isDeleted = false")
    List<Role> findRolesByUserId(@Param("userId") UUID userId);

    // Get all users for a specific role
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findUsersByRoleName(@Param("roleName") String roleName);

    // Check if user has a specific role
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Role r JOIN r.users u WHERE u.userId = :userId AND r.name = :roleName")
    boolean userHasRole(@Param("userId") UUID userId, @Param("roleName") String roleName);

    // Eager fetch users with roles
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles")
    List<User> findAllWithRoles();

    // Count users by role
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = :roleName")
    long countUsersByRole(@Param("roleName") String roleName);

}
