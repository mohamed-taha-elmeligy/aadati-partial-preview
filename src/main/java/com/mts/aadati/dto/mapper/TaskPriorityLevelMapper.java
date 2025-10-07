package com.mts.aadati.dto.mapper;

import com.mts.aadati.entities.TaskPriorityLevel;
import com.mts.aadati.dto.request.TaskPriorityLevelRequest;
import com.mts.aadati.dto.response.TaskPriorityLevelResponse;

/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */

public class TaskPriorityLevelMapper {

    private TaskPriorityLevelMapper() {}

    public static TaskPriorityLevel toEntity(TaskPriorityLevelRequest request) {
        return TaskPriorityLevel.builder()
                .priorityLevel(request.getPriorityLevel())
                .name(request.getName())
                .color(request.getColor())
                .build();
    }

    public static TaskPriorityLevelResponse toResponse(TaskPriorityLevel entity) {
        return TaskPriorityLevelResponse.builder()
                .taskPriorityLevelId(entity.getTaskPriorityLevelId())
                .priorityLevel(entity.getPriorityLevel())
                .name(entity.getName())
                .color(entity.getColor())
                .build();
    }
}

