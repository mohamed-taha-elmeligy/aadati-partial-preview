package com.mts.aadati.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
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
@Getter
@ToString(exclude = "habitTasks")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "task_priority_level",
        indexes = {
                @Index(name = "inx_task_priority_level_level", columnList = "priority_level"),
                @Index(name = "inx_task_priority_level_name", columnList = "name")
        })
public class TaskPriorityLevel {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "task_priority_level_id", nullable = false, updatable = false,unique = true, columnDefinition = "UUID")
    private UUID taskPriorityLevelId;

    @Column(name = "priority_level", nullable = false, unique = true)
    @Min(value = 1, message = "Priority level must be at least 1")
    @Max(value = 10, message = "Priority level cannot exceed 10")
    @Setter
    private int priorityLevel;

    @Column(name = "name", nullable = false, length = 50,unique = true)
    @NotBlank(message = "Priority name is required")
    @Size(min = 2, max = 50, message = "Priority name must be between 2 and 50 characters")
    @Setter
    private String name;

    @Setter
    @Column(name = "color", nullable = false, length = 7 , unique = true)
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color must be a valid hex code like #FFFFFF")
    private String color;

    // ===== Relationship =====
    @JsonIgnore
    @OneToMany(mappedBy = "taskPriorityLevel" , fetch = FetchType.LAZY ,
            cascade = {CascadeType.DETACH ,CascadeType.MERGE ,CascadeType.PERSIST ,CascadeType.REFRESH} )
    private final List<HabitTask> habitTasks = new ArrayList<>();


    // ===== Builder Constructor =====
    @Builder
    public TaskPriorityLevel(int priorityLevel ,@NonNull String name,@NonNull String color) {
        this.priorityLevel = priorityLevel;
        this.name = name;
        this.color = color;
    }
    private TaskPriorityLevel(int priorityLevel) {
        this.priorityLevel = priorityLevel;
        this.name = generateNameByLevel(priorityLevel);
        this.color = generateColorByLevel(priorityLevel);
    }

    // ===== Helper Methods =====
    private String generateColorByLevel(int level) {
        return switch (level) {
            case 1 -> "#FF4444";
            case 2 -> "#FF8800";
            case 3 -> "#FFD700";
            case 4 -> "#00CC66";
            case 5 -> "#00AAFF";
            default -> "#888888";
        };
    }
    private String generateNameByLevel(int level) {
        return switch (level) {
            case 1 -> "Urgent";
            case 2 -> "High";
            case 3 -> "Important";
            case 4 -> "Medium";
            case 5 -> "Low";
            default -> "Custom-" + level;
        };
    }

    @JsonIgnore
    public boolean isHighPriority() {
        return priorityLevel <= 5;
    }
    @JsonIgnore
    public boolean isLowPriority() {
        return priorityLevel >= 6;
    }

    // ===== Static Factory Methods =====
    public static TaskPriorityLevel createUrgent() {
        return new  TaskPriorityLevel(1);
    }

    public static TaskPriorityLevel createHigh() {
        return new  TaskPriorityLevel(2);
    }

    public static TaskPriorityLevel createImportant() {
        return new  TaskPriorityLevel(3);
    }

    public static TaskPriorityLevel createMedium() {
        return new  TaskPriorityLevel(4);
    }

    public static TaskPriorityLevel createLow() {
        return new  TaskPriorityLevel(5);
    }
}