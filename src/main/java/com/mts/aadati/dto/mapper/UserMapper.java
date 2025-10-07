package com.mts.aadati.dto.mapper;

import com.mts.aadati.entities.Role;
import com.mts.aadati.entities.User;
import com.mts.aadati.dto.request.UserRequest;
import com.mts.aadati.dto.response.UserResponse;
import com.mts.aadati.services.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */

@AllArgsConstructor
@Component
public class UserMapper {

    private final RoleService roleService ;

    public User toEntity(UserRequest request) {
        User user =  User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .emailVerified(true)
                .build();
        user.setRoles(roleService.findEntityByName("ROLE_USER").stream().toList());
        return user ;
    }

    public static UserResponse toResponse(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .email(user.getEmail())
                .emailVerified(user.isEmailVerified())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .roles(user.getRoles().stream().map(Role::getName).toList())
                .build();
    }
}

