package com.mts.aadati.dto.response;

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
@Builder
@ToString
public class HabitDayWeekResponse {

    private long dayWeekId;
    private DayOfWeek dayOfWeek;
}

