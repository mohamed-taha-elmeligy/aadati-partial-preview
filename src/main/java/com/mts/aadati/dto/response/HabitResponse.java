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
public class HabitResponse {

    private UUID habitId;
    private String title;
    private double point;
    private boolean type;
    private String description;
    private boolean isActive;
    private UUID userId;
    private UUID habitCategoryId;
    private List<Long> habitDayWeekIds;
    private Instant updatedAt;
    private Instant createdAt;
}
