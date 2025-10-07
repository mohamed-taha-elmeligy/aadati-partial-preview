package com.mts.aadati.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */

@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class HabitCategoryRequest {

    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 50, message = "Category name must be between 2 and 50 characters")
    private String name;

    @Size(max = 800, message = "Description cannot exceed 800 characters")
    private String description;

    private String color;
}

