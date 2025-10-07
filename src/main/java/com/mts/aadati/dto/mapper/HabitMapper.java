package com.mts.aadati.dto.mapper;

import com.mts.aadati.entities.Habit;
import com.mts.aadati.entities.HabitCategory;
import com.mts.aadati.entities.HabitDayWeek;
import com.mts.aadati.entities.User;
import com.mts.aadati.dto.request.HabitRequest;
import com.mts.aadati.dto.response.HabitResponse;

import java.util.Collections;
import java.util.List;

/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */

public class HabitMapper {

    private HabitMapper() {
    }

    public static Habit toEntity(HabitRequest request, User user, HabitCategory habitCategory) {
        if (request == null) {
            throw new IllegalArgumentException("HabitRequest cannot be null");
        }
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (habitCategory == null) {
            throw new IllegalArgumentException("HabitCategory cannot be null");
        }

        return Habit.builder()
                .title(request.getTitle())
                .point(request.getPoint())
                .type(request.isType())
                .description(request.getDescription())
                .isActive(request.isActive())
                .user(user)
                .habitCategory(habitCategory)
                .build();
    }

    public static HabitResponse toResponse(Habit habit) {
        if (habit == null) {
            throw new IllegalArgumentException("Habit cannot be null");
        }

        return HabitResponse.builder()
                .habitId(habit.getHabitId())
                .title(habit.getTitle())
                .point(habit.getPoint())
                .type(habit.isPositiveHabit())
                .description(habit.getDescription())
                .isActive(habit.canBeCompleted())
                .userId(habit.getUser() != null ? habit.getUser().getUserId() : null)
                .habitCategoryId(habit.getHabitCategory() != null ?
                        habit.getHabitCategory().getHabitCategoryId() : null)
                .habitDayWeekIds(mapHabitDayWeeksToIds(habit.getHabitDayWeeks()))
                .updatedAt(habit.getUpdatedAt())
                .createdAt(habit.getCreatedAt())
                .build();
    }

    private static List<Long> mapHabitDayWeeksToIds(List<HabitDayWeek> habitDayWeeks) {
        if (habitDayWeeks == null || habitDayWeeks.isEmpty()) {
            return Collections.emptyList();
        }

        return habitDayWeeks.stream()
                .map(HabitDayWeek::getDayWeekId)
                .toList();
    }

}