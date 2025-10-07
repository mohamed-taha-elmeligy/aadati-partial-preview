
package com.mts.aadati.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */

@NoArgsConstructor
@Getter @EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Entity @Table(name ="habit_completion" , indexes = @Index(name = "inx_habit_completion_checker" , columnList = "complete"))
public class HabitCompletion {

    @Id
    @Setter
    @ToString.Include
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "habit_completion_id", nullable = false , updatable = false ,columnDefinition = "UUID")
    private UUID habitCompletionId;

    @ToString.Include
    @Column(name = "complete", nullable = false)
    @Setter
    private boolean complete ;

    @ToString.Include
    @Column(name = "completed_at"  )
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant completedAt;

    @ToString.Include
    @JsonIgnore
    @Column(name = "created_at"  )
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant createdAt;

    // =====  Relationship =====
    @ToString.Exclude
    @JsonIgnore
    @JoinColumn(name = "habit_calendar_id" , nullable = false)
    @ManyToOne( fetch = FetchType.LAZY )
    private HabitCalendar habitCalendar ;

    @ToString.Exclude
    @JsonIgnore
    @JoinColumn(name = "habit_id" ,nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Habit habit ;

    // ===== Builder Constructor ======
    @Builder
    public HabitCompletion (boolean complete ,@NonNull HabitCalendar habitCalendar ,@NonNull Habit habit){
        this.complete = complete;
        this.habitCalendar = habitCalendar ;
        this.habit = habit ;
    }

    // ===== Helper Method for Completed ======
    public boolean isCompleted() {
        return complete;
    }
    public String getCompleteText() {
        return complete ? "Completed" : "Uncompleted";
    }
    public HabitCompletion markIncomplete() {
        this.complete = false;
        return this ;
    }
    public HabitCompletion markComplete() {
        this.complete = true;
        return this ;
    }

    // ===== Lifecycle Callback ======
    @PrePersist
    private void onCreate() {
        this.createdAt = Instant.now();
        if (this.complete && this.completedAt == null) {
            this.completedAt = Instant.now();
        }
    }
    @PreUpdate
    private void onUpdate() {
        if (this.complete && this.completedAt == null) {
            this.completedAt = Instant.now();
        } else if (!this.complete) {
            this.completedAt = null;
        }
    }



}
