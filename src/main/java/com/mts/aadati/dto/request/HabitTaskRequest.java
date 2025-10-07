package com.mts.aadati.dto.request;

import com.mts.aadati.enums.RecurrenceType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */

@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class HabitTaskRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 2, max = 50, message = "Title must be between 2 and 50 characters")
    private String title;

    @Size(max = 800, message = "Description cannot exceed 800 characters")
    private String description;

    private boolean isActive = true;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be now or in the future")
    private Instant startDate;

    @NotNull(message = "Recurrence type is required")
    private RecurrenceType recurrenceType;

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotNull(message = "TaskPriorityLevel ID is required")
    private UUID taskPriorityLevelId;

    @NotNull(message = "HabitCategory ID is required")
    private UUID habitCategoryId;

    private List<Long> habitDayWeekIds;
}
