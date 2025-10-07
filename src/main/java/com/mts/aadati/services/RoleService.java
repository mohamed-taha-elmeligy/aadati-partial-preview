package com.mts.aadati.services;

import com.mts.aadati.dto.mapper.RoleMapper;
import com.mts.aadati.dto.request.RoleRequest;
import com.mts.aadati.dto.response.RoleResponse;
import com.mts.aadati.entities.Role;
import com.mts.aadati.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */
@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private static final Logger logger = LoggerFactory.getLogger(RoleService.class);

    // ===== CRUD =====
    public boolean addOrUpdateRole(Role role) {
        logger.debug("addOrUpdateRole called with: {}", role);
        if (role == null) {
            logger.warn("addOrUpdateRole failed: role is null");
            return false;
        }
        Role savedRole = roleRepository.save(role);
        logger.info("Role saved/updated successfully: {}", savedRole.getName());
        return true;
    }

    public boolean addOrUpdateRoleList(List<Role> roles) {
        logger.debug("addOrUpdateRoleList called with {} roles", roles != null ? roles.size() : 0);
        if (roles == null || roles.isEmpty()) {
            logger.warn("addOrUpdateRoleList failed: roles is null or empty");
            return false;
        }

        List<Role> rolesToSave = new ArrayList<>();
        for (Role role : roles) {
            Optional<Role> existingRole = roleRepository.findByNameIgnoreCase(role.getName());
            if (existingRole.isEmpty()) {
                rolesToSave.add(role);
            } else {
                logger.debug("Role {} already exists, skipping", role.getName());
            }
        }

        if (!rolesToSave.isEmpty()) {
            roleRepository.saveAll(rolesToSave);
            logger.info("Role list saved successfully ({} new records)", rolesToSave.size());
        } else {
            logger.info("All roles already exist, nothing to save");
        }

        return true;
    }

    public boolean addOrUpdateRoleRequests(List<RoleRequest> roleRequests) {
        logger.debug("addOrUpdateRoleRequests called with {} requests", roleRequests != null ? roleRequests.size() : 0);
        if (roleRequests == null || roleRequests.isEmpty()) {
            logger.warn("addOrUpdateRoleRequests failed: requests is null or empty");
            return false;
        }

        List<Role> rolesToSave = roleRequests.stream()
                .map(RoleMapper::toEntity)
                .map(role -> roleRepository.findByNameIgnoreCase(role.getName())
                        .map(existing -> {
                            existing.setDescription(role.getDescription());
                            return existing;
                        })
                        .orElse(role))
                .toList();

        roleRepository.saveAll(rolesToSave);
        logger.info("addOrUpdateRoleRequests saved {} roles", rolesToSave.size());
        return true;
    }

    public boolean deleteRole(UUID roleId) {
        logger.debug("deleteRole called with: {}", roleId);
        return roleRepository.findById(roleId)
                .map(role -> {
                    role.setDeleted(true);
                    roleRepository.save(role);
                    logger.info("Role deleted successfully: {}", role.getName());
                    return true;
                })
                .orElseGet(() -> {
                    logger.warn("Role delete failed: role not found for ID {}", roleId);
                    return false;
                });
    }

    // ===== Find =====
    @Transactional(readOnly = true)
    public Optional<RoleResponse> findByName(String name) {
        logger.debug("findByName called with: {}", name);
        if (name == null || name.isBlank()) {
            logger.warn("findByName failed: name is null/blank");
            return Optional.empty();
        }
        return roleRepository.findByNameIgnoreCase(name.trim())
                .map(RoleMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Optional<Role> findEntityByName(String name) {
        logger.debug("findByName : called with :{} ", name);
        if (name == null || name.isBlank()) {
            logger.warn("findByName failed: name is Blank or Empty");
            return Optional.empty(); }
        logger.info("findByName successfully");
        return roleRepository.findByNameIgnoreCase(name.trim());
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> searchByName(String name) {
        logger.debug("searchByName called with: {}", name);
        if (name == null || name.isBlank()) {
            logger.warn("searchByName failed: name is null/blank");
            return Collections.emptyList();
        }
        return roleRepository.findByNameContainingIgnoreCase(name.trim())
                .stream()
                .map(RoleMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<RoleResponse> findById(UUID roleId) {
        logger.debug("findById called with: {}", roleId);
        if (roleId == null) {
            logger.warn("findById failed: roleId is null");
            return Optional.empty();
        }
        return roleRepository.findById(roleId)
                .map(RoleMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> findIsDeleteFalse() {
        logger.debug("findIsDeleteFalse called");
        return roleRepository.findAllByIsDeletedFalse()
                .stream()
                .map(RoleMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Role> findEntityIsDeleteFalse() {
        logger.debug("findEntityIsDeleteFalse called");
        return roleRepository.findAllByIsDeletedFalse() ;
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> findIsDeleteTrue() {
        logger.debug("findIsDeleteTrue called");
        return roleRepository.findAllByIsDeletedTrue()
                .stream()
                .map(RoleMapper::toResponse)
                .toList();
    }

    // ===== Count =====
    public long countActiveRoles() {
        logger.debug("countActiveRoles called");
        return roleRepository.countByIsDeletedFalse();
    }

    // ===== Exists =====
    public boolean existsByName(String name) {
        logger.debug("existsByName called with: {}", name);
        if (name == null || name.isBlank()) {
            logger.warn("existsByName failed: name is null/blank");
            return false;
        }
        return roleRepository.existsByNameAndIsDeletedFalse(name.trim());
    }

    public boolean existsByRoleId(UUID roleId) {
        logger.debug("existsByRoleId called with: {}", roleId);
        if (roleId == null) {
            logger.warn("existsByRoleId failed: roleId is null");
            return false;
        }
        return roleRepository.existsByRoleIdAndIsDeletedFalse(roleId);
    }
}
