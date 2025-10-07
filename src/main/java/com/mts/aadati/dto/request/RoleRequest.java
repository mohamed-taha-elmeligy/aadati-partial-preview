package com.mts.aadati.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */

@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RoleRequest {

    @ToString.Include
    @Size(max = 50, min = 3, message = "Name must be between 3 and 50 characters")
    @NotBlank(message = "Name is required")
    private String name;

    @ToString.Include
    @Size(max = 1000, message = "Description must be between 0 and 1000 characters")
    private String description;
}

