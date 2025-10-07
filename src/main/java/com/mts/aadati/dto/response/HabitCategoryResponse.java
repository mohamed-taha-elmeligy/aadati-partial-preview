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
public class HabitCategoryResponse {

    private UUID habitCategoryId;
    private String name;
    private String description;
    private String color;
    private Instant updatedAt;
    private Instant createdAt;
}

