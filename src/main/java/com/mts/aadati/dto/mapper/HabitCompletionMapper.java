package com.mts.aadati.dto.mapper;

import com.mts.aadati.entities.Habit;
import com.mts.aadati.entities.HabitCalendar;
import com.mts.aadati.entities.HabitCompletion;
import com.mts.aadati.dto.request.HabitCompletionRequest;
import com.mts.aadati.dto.response.HabitCompletionResponse;

/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */

public class HabitCompletionMapper {

    private HabitCompletionMapper() {}

    public static HabitCompletion toEntity(HabitCompletionRequest request, HabitCalendar habitCalendar, Habit habit) {
        return HabitCompletion.builder()
                .complete(request.isComplete())
                .habitCalendar(habitCalendar)
                .habit(habit)
                .build();
    }

    public static HabitCompletionResponse toResponse(HabitCompletion habitCompletion) {
        return HabitCompletionResponse.builder()
                .habitCompletionId(habitCompletion.getHabitCompletionId())
                .habitCalendarId(habitCompletion.getHabitCalendar().getHabitCalendarId())
                .habitId(habitCompletion.getHabit().getHabitId())
                .complete(habitCompletion.isCompleted())
                .completedAt(habitCompletion.getCompletedAt())
                .build();
    }
}
