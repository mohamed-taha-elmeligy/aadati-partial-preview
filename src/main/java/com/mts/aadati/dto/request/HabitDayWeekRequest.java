package com.mts.aadati.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.DayOfWeek;

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
public class HabitDayWeekRequest {

    @NotNull(message = "Day of week is required")
    private DayOfWeek dayOfWeek;
}

