
package com.mts.aadati.repository;

import com.mts.aadati.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    // ===== Find =====
    List<Role> findByNameContainingIgnoreCase(String name);
    Optional<Role> findByNameIgnoreCase(String name);
    List<Role> findAllByIsDeletedFalse();
    List<Role> findAllByIsDeletedTrue();

    // ===== count =====
    long countByIsDeletedFalse();

    // ===== Exists =====
    boolean existsByNameAndIsDeletedFalse(String name );
    boolean existsByRoleIdAndIsDeletedFalse(UUID roleId);

}
