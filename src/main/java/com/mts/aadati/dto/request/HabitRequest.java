package com.mts.aadati.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class HabitRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 50, message = "Title must be between 3 and 50 characters")
    @Pattern(regexp = "^[\\p{L}\\p{M}\\s.'-]+$",
            message = "Title can only contain letters, spaces, dots, hyphens and apostrophes")
    private String title;

    @DecimalMin(value = "0.5", message = "Minimum point value is 0.5")
    @DecimalMax(value = "10.0", message = "Maximum point value is 10.0")
    @Builder.Default
    private double point = 1.0;

    @Builder.Default
    private boolean type = true;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @Builder.Default
    private boolean isActive = true;

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotNull(message = "HabitCategory ID is required")
    private UUID habitCategoryId;

    @Builder.Default
    private List<Long> habitDayWeekIds = new ArrayList<>();

    public boolean isPositiveHabit() {
        return type;
    }

    public String getHabitTypeText() {
        return type ? "Positive" : "Negative";
    }

    public boolean hasDayRestrictions() {
        return habitDayWeekIds != null && !habitDayWeekIds.isEmpty();
    }

    public int getDayCount() {
        return habitDayWeekIds != null ? habitDayWeekIds.size() : 0;
    }

    public boolean isValidPoint() {
        return point >= 0.5 && point <= 10.0;
    }

    public String getPointsText() {
        return point + (point == 1.0 ? " Point" : " Points");
    }

    public void addDayWeek(Long dayWeekId) {
        if (habitDayWeekIds == null) {
            habitDayWeekIds = new ArrayList<>();
        }
        if (!habitDayWeekIds.contains(dayWeekId)) {
            habitDayWeekIds.add(dayWeekId);
        }
    }

    public void removeDayWeek(Long dayWeekId) {
        if (habitDayWeekIds != null) {
            habitDayWeekIds.remove(dayWeekId);
        }
    }

    public void clearDays() {
        if (habitDayWeekIds != null) {
            habitDayWeekIds.clear();
        }
    }
}