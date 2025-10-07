package com.mts.aadati.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.NaturalId;

import java.time.Instant;
import java.util.*;

/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */
@NoArgsConstructor
@Getter @ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity @Table(name = "habit",
        uniqueConstraints = @UniqueConstraint(columnNames = {"title","user_id"}),
        indexes = {
        @Index(name = "inx_habit_title", columnList = "title"),
        @Index(name = "inx_habit_type", columnList = "type"),
        @Index(name = "inx_habit_point", columnList = "point")
})
public class Habit {

    @EqualsAndHashCode.Include
    @Id
    @Setter
    @ToString.Include
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "habit_id", nullable = false, updatable = false, columnDefinition = "UUID")
    private UUID habitId;

    @ToString.Include
    @Column(name = "title", length = 50, nullable = false)
    @Size(max = 50, min = 3, message = "Title must be between 3 and 50 characters")
    @NotBlank(message = "Title is required")
    @Pattern(regexp = "^[\\p{L}\\p{M}\\s.'-]+$", message = "Title can only contain letters, spaces, dots, hyphens and apostrophes")
    @Setter
    @NaturalId
    private String title;

    @ToString.Include
    @Column(name = "point", nullable = false)
    @DecimalMin(value = "0.5", message = "Minimum value is 0.5")
    @DecimalMax(value = "10.0", message = "Maximum value is 10.0")
    @Setter
    private double point = 1.0;

    @ToString.Include
    @Column(name = "type", nullable = false)
    @Setter
    private boolean type;

    @ToString.Include
    @Column(name = "description", length = 1000)
    @Size(max = 1000, message = "Description must be between 0 and 1000 characters")
    @Setter
    private String description;

    @ToString.Include
    @Column(name = "is_active", nullable = false)
    @Setter
    private boolean isActive;

    @ToString.Include
    @Column(name = "updated_at" , nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant updatedAt;

    @ToString.Include
    @Column(name = "created_at" , nullable = false , updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant createdAt;

    // =====  Relationship =====
    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "habit" , fetch = FetchType.LAZY , orphanRemoval = true , cascade = CascadeType.ALL)
    private final List<HabitCompletion> habitCompletions = new ArrayList<>();

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id" , nullable = false)
    @Setter
    private User user ;

    @ToString.Exclude
    @JsonIgnore
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habit_category_id" , nullable = false)
    private HabitCategory habitCategory ;

    @ToString.Exclude
    @ManyToMany(cascade = {CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH},
    fetch = FetchType.LAZY )
    @JoinTable(name = "habit_day_of_week",
            joinColumns = @JoinColumn(name = "habit_id" ,nullable = false),
            inverseJoinColumns = @JoinColumn(name ="day_week_id",nullable = false ))
    private final List<HabitDayWeek> habitDayWeeks = new ArrayList<>();

    // ===== Builder Constructor =====

    @Builder
    public Habit(@NonNull String title,
                 double point, boolean type,
                 String description,
                 boolean isActive,
                 @NonNull User user ,
                 @NonNull HabitCategory habitCategory ) {
        this.title = title;
        this.point = (point > 0) ? point : 1.0;
        this.type = type ;
        this.description = description;
        this.isActive =  isActive;
        this.user = user ;
        this.habitCategory = habitCategory ;
    }

    // ===== Helper Method for Type =====
    public boolean isPositiveHabit() {
        return type;
    }
    public String getHabitTypeText() {
        return type ? "Positive" : "Negative";
    }

    // ===== Helper Method for Point =====
    public String getPointsText() {
        return point + (point == 1.0 ? " Point" : " Points");
    }

    // ===== Helper Method for is Active =====
    public boolean canBeCompleted() {
        return isActive;
    }
    public Habit activate() {
        this.isActive = true;
        return this;
    }
    public Habit deactivate() {
        this.isActive = false;
        return this;
    }

    // ===== Lifecycle Callback ======
    @PrePersist
    private void onCreate() {
        Instant now = Instant.now();
        updatedAt = now ;
        createdAt = now ;
    }
    @PreUpdate
    private void onUpdate() {
        updatedAt = Instant.now();
    }
}
