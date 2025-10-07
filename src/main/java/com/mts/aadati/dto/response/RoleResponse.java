package com.mts.aadati.dto.response;

import lombok.*;
import java.time.Instant;
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
public class RoleResponse {
    private UUID roleId;
    private String name;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
}
