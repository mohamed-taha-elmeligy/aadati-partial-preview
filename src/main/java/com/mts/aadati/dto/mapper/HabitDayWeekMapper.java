package com.mts.aadati.dto.mapper;

import com.mts.aadati.entities.HabitDayWeek;
import com.mts.aadati.dto.request.HabitDayWeekRequest;
import com.mts.aadati.dto.response.HabitDayWeekResponse;

/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */

public class HabitDayWeekMapper {

    private HabitDayWeekMapper() {}

    public static HabitDayWeek toEntity(HabitDayWeekRequest request) {
        return HabitDayWeek.builder()
                .dayOfWeek(request.getDayOfWeek())
                .build();
    }

    public static HabitDayWeekResponse toResponse(HabitDayWeek entity) {
        return HabitDayWeekResponse.builder()
                .dayWeekId(entity.getDayWeekId())
                .dayOfWeek(entity.getDayOfWeek())
                .build();
    }
}

