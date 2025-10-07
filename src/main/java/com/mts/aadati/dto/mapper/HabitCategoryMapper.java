package com.mts.aadati.dto.mapper;

import com.mts.aadati.entities.HabitCategory;
import com.mts.aadati.dto.request.HabitCategoryRequest;
import com.mts.aadati.dto.response.HabitCategoryResponse;

/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */

public class HabitCategoryMapper {

    private HabitCategoryMapper() {}

    public static HabitCategory toEntity(HabitCategoryRequest request) {
        return HabitCategory.builder()
                .name(request.getName())
                .description(request.getDescription())
                .color(request.getColor())
                .build();
    }

    public static HabitCategoryResponse toResponse(HabitCategory habitCategory) {
        return HabitCategoryResponse.builder()
                .habitCategoryId(habitCategory.getHabitCategoryId())
                .name(habitCategory.getName())
                .description(habitCategory.getDescription())
                .color(habitCategory.getColor())
                .updatedAt(habitCategory.getUpdatedAt())
                .createdAt(habitCategory.getCreatedAt())
                .build();
    }
}

