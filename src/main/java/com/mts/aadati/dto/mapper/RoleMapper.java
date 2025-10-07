package com.mts.aadati.dto.mapper;

import com.mts.aadati.dto.request.RoleRequest;
import com.mts.aadati.dto.response.RoleResponse;
import com.mts.aadati.entities.Role;

/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */

public class RoleMapper {

    private RoleMapper() {
    }

    public static Role toEntity(RoleRequest request) {
        return Role.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }

    public static RoleResponse toResponse(Role role) {
        return RoleResponse.builder()
                .roleId(role.getRoleId())
                .name(role.getName())
                .description(role.getDescription())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .build();
    }
}