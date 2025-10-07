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
public class HabitCompletionResponse {

    private UUID habitCompletionId;
    private UUID habitCalendarId;
    private UUID habitId;
    private boolean complete;
    private Instant completedAt;
}

