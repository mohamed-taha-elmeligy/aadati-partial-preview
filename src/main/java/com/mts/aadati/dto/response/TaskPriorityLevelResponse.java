package com.mts.aadati.dto.response;

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
@Builder
@ToString
public class TaskPriorityLevelResponse {

    private UUID taskPriorityLevelId;
    private int priorityLevel;
    private String name;
    private String color;
}

