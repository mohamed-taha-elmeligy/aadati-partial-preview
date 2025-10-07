package com.mts.aadati.dto.mapper;

import com.mts.aadati.entities.HabitTask;
import com.mts.aadati.entities.HabitCategory;
import com.mts.aadati.entities.TaskPriorityLevel;
import com.mts.aadati.entities.User;
import com.mts.aadati.dto.request.HabitTaskRequest;
import com.mts.aadati.dto.response.HabitTaskResponse;

/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */

public class HabitTaskMapper {

    private HabitTaskMapper() {}

    public static HabitTask toEntity(HabitTaskRequest request, User user,
                                     TaskPriorityLevel taskPriorityLevel,
                                     HabitCategory habitCategory) {
        return HabitTask.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .isActive(request.isActive())
                .startDate(request.getStartDate())
                .recurrenceType(request.getRecurrenceType())
                .user(user)
                .taskPriorityLevel(taskPriorityLevel)
                .habitCategory(habitCategory)
                .build();
    }

    public static HabitTaskResponse toResponse(HabitTask habitTask) {
        return HabitTaskResponse.builder()
                .habitTaskId(habitTask.getHabitTaskId())
                .title(habitTask.getTitle())
                .description(habitTask.getDescription())
                .isActive(habitTask.canBeCompleted())
                .startDate(habitTask.getStartDate())
                .recurrenceType(habitTask.getRecurrenceType())
                .userId(habitTask.getUser().getUserId())
                .taskPriorityLevelId(habitTask.getTaskPriorityLevel().getTaskPriorityLevelId())
                .habitCategoryId(habitTask.getHabitCategory().getHabitCategoryId())
                .updatedAt(habitTask.getUpdatedAt())
                .createdAt(habitTask.getCreatedAt())
                .build();
    }
}
