package com.mts.aadati.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mts.aadati.enums.RecurrenceType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.NaturalId;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */

@NoArgsConstructor
@Getter @ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity @Table(
        name = "habit_task",
        uniqueConstraints = @UniqueConstraint(columnNames = {"title","user_id"}),
        indexes = {
                @Index(name = "inx_habit_task_title", columnList = "title"),
                @Index(name = "inx_habit_task_start_date", columnList = "start_date"),
                @Index(name = "inx_habit_task_recurrence_type", columnList = "recurrence_type")}
)
public class HabitTask {

    @Id
    @Setter
    @ToString.Include
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "habit_task_id", nullable = false, updatable = false, columnDefinition = "UUID")
    private UUID habitTaskId;

    @ToString.Include
    @Column(name = "title", nullable = false, length = 50)
    @NotBlank(message = "Title is required")
    @Size(min = 2, max = 50, message = "Title must be between 2 and 50 characters")
    @Pattern(regexp = "^[\\p{L}\\p{M}\\s.'-]+$", message = "Title can only contain letters, spaces, dots, hyphens and apostrophes")
    @Setter
    @NaturalId
    private String title;

    @ToString.Include
    @Column(name = "description", length = 800)
    @Size(max = 800, message = "Description cannot exceed 800 characters")
    @Setter
    private String description;

    @ToString.Include
    @Column(name = "recurrence_type", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @NotNull(message = "Recurrence type is required")
    @Setter
    private RecurrenceType recurrenceType;

    @ToString.Include
    @Column(name = "isActive", nullable = false)
    @Setter
    private boolean isActive;

    @ToString.Include
    @Column(name = "start_date", nullable = false)
    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be now or in the future")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @Setter
    private Instant startDate;

    @ToString.Include
    @Column(name = "updated_at", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant updatedAt;

    @ToString.Include
    @Column(name = "created_at", updatable = false, nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant createdAt;

    // =====  Relationship =====
    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "habitTask" , cascade = CascadeType.ALL ,orphanRemoval = true , fetch = FetchType.LAZY)
    private final List<TaskCompletion> taskCompletions = new ArrayList<>();

    @ToString.Exclude
    @Setter
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id" , nullable = false)
    private User user ;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_priority_level_id" , nullable = false)
    private TaskPriorityLevel taskPriorityLevel ;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habit_category_id" , nullable = false)
    private HabitCategory habitCategory ;

    // ===== Builder Constructor =====
    @Builder
    public HabitTask(@NonNull String title,
                     String description,
                     boolean isActive,
                     @NonNull Instant startDate,
                     @NonNull RecurrenceType recurrenceType ,
                     @NonNull User user,
                     @NonNull TaskPriorityLevel taskPriorityLevel,
                     @NonNull HabitCategory habitCategory) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.recurrenceType = recurrenceType;
        this.user = user ;
        this.taskPriorityLevel = taskPriorityLevel ;
        this.habitCategory = habitCategory ;
        this.isActive =  isActive;
    }

    // ===== Helper Method for is Active =====
    public boolean canBeCompleted() {
        return isActive;
    }
    public HabitTask activate() {
        this.isActive = true;
        return this;
    }
    public HabitTask deactivate() {
        this.isActive = false;
        return this;
    }

    // في HabitTask entity
    public DayOfWeek getScheduledDayOfWeek() {
        return startDate.atZone(ZoneOffset.UTC)
                .toLocalDate()
                .getDayOfWeek();
    }

    public boolean isScheduledForDay(DayOfWeek targetDay) {
        return recurrenceType == RecurrenceType.WEEKLY &&
                getScheduledDayOfWeek().equals(targetDay);
    }


    // ===== Helper Method for Lifecycle =====
    @PreUpdate
    private void perUpdates() {
        updatedAt = Instant.now();
    }

    @PrePersist
    private void perPersist() {
        createdAt = Instant.now();
        updatedAt = createdAt;
    }
}
