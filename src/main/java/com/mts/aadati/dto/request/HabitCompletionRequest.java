package com.mts.aadati.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class HabitCompletionRequest {

    @NotNull(message = "HabitCalendar ID is required")
    private UUID habitCalendarId;

    @NotNull(message = "Habit ID is required")
    private UUID habitId;

    private boolean complete;
}

