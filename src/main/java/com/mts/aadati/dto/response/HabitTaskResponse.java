package com.mts.aadati.dto.response;

import com.mts.aadati.enums.RecurrenceType;
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
public class HabitTaskResponse {

    private UUID habitTaskId;
    private String title;
    private String description;
    private boolean isActive;
    private Instant startDate;
    private RecurrenceType recurrenceType;
    private UUID userId;
    private UUID taskPriorityLevelId;
    private UUID habitCategoryId;
    private Instant updatedAt;
    private Instant createdAt;
}

