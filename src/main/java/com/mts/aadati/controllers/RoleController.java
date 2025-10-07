package com.mts.aadati.controllers;

import com.mts.aadati.dto.mapper.RoleMapper;
import com.mts.aadati.dto.request.RoleRequest;
import com.mts.aadati.dto.response.RoleResponse;
import com.mts.aadati.entities.Role;
import com.mts.aadati.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */
@RestController
@RequestMapping("/aadati/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);

    // ===== Add / Update Role =====
    @PostMapping("/add")
    public ResponseEntity<Boolean> addRole(@RequestBody RoleRequest request) {
        logger.debug("addRole called with request: {}", request);
        if (request == null) {
            logger.warn("addRole failed: request is null");
            return ResponseEntity.badRequest().body(false);
        }
        Role role = RoleMapper.toEntity(request);
        boolean result = roleService.addOrUpdateRole(role);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/add-list")
    public ResponseEntity<Boolean> addRoleList(@RequestBody List<RoleRequest> requests) {
        logger.debug("addRoleList called with {} requests", requests != null ? requests.size() : 0);
        boolean result = roleService.addOrUpdateRoleRequests(requests);
        return ResponseEntity.ok(result);
    }

    // ===== Delete Role =====
    @DeleteMapping("/delete/{roleId}")
    public ResponseEntity<Boolean> deleteRole(@PathVariable UUID roleId) {
        logger.debug("deleteRole called with roleId: {}", roleId);
        boolean result = roleService.deleteRole(roleId);
        return ResponseEntity.ok(result);
    }

    // ===== Find by ID =====
    @GetMapping("/search/{roleId}")
    public ResponseEntity<RoleResponse> findById(@PathVariable UUID roleId) {
        logger.debug("findById called with roleId: {}", roleId);
        Optional<RoleResponse> roleOpt = roleService.findById(roleId);
        return roleOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ===== Find by Name =====
    @GetMapping("/search/name/{name}")
    public ResponseEntity<RoleResponse> findByName(@PathVariable String name) {
        logger.debug("findByName called with name: {}", name);
        Optional<RoleResponse> roleOpt = roleService.findByName(name);
        return roleOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search/name/like/{name}")
    public ResponseEntity<List<RoleResponse>> searchByName(@PathVariable String name) {
        logger.debug("searchByName called with name: {}", name);
        List<RoleResponse> roles = roleService.searchByName(name);
        return ResponseEntity.ok(roles);
    }

    // ===== Find Active / Deleted Roles =====
    @GetMapping("/active")
    public ResponseEntity<List<RoleResponse>> getActiveRoles() {
        logger.debug("getActiveRoles called");
        List<RoleResponse> roles = roleService.findIsDeleteFalse();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/deleted")
    public ResponseEntity<List<RoleResponse>> getDeletedRoles() {
        logger.debug("getDeletedRoles called");
        List<RoleResponse> roles = roleService.findIsDeleteTrue();
        return ResponseEntity.ok(roles);
    }

    // ===== Count Active Roles =====
    @GetMapping("/count/active")
    public ResponseEntity<Long> countActiveRoles() {
        logger.debug("countActiveRoles called");
        long count = roleService.countActiveRoles();
        return ResponseEntity.ok(count);
    }

    // ===== Exists Checks =====
    @GetMapping("/exists/name/{name}")
    public ResponseEntity<Boolean> existsByName(@PathVariable String name) {
        logger.debug("existsByName called with name: {}", name);
        boolean exists = roleService.existsByName(name);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/exists/id/{roleId}")
    public ResponseEntity<Boolean> existsById(@PathVariable UUID roleId) {
        logger.debug("existsById called with roleId: {}", roleId);
        boolean exists = roleService.existsByRoleId(roleId);
        return ResponseEntity.ok(exists);
    }
}
