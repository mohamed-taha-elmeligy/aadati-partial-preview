package com.mts.aadati.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

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
public class TaskPriorityLevelRequest {

    @Min(1)
    @Max(10)
    private int priorityLevel;

    @NotBlank
    @Size(min = 2, max = 50)
    private String name;

    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$")
    private String color;
}

