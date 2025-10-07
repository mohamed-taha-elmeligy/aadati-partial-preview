package com.mts.aadati.dto.response;

import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserResponse {
    private UUID userId;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private boolean emailVerified;
    private Instant updatedAt;
    private Instant createdAt;
    private List<String> roles;

}

