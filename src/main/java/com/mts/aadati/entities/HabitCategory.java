package com.mts.aadati.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */

@NoArgsConstructor
@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "habit_category",
        indexes = @Index(name = "inx_habit_category_name", columnList = "name"))
public class HabitCategory {

    @ToString.Include
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "habit_category_id", nullable = false , updatable = false ,columnDefinition = "UUID")
    private UUID habitCategoryId;

    @ToString.Include
    @Column(name = "name", nullable = false, unique = true, length = 50)
    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 50, message = "Category name must be between 2 and 50 characters")
    @Setter
    private String name;

    @ToString.Include
    @Column(name = "description", length = 800)
    @Size(max = 800, message = "Description cannot exceed 800 characters")
    @Setter
    private String description;

    @ToString.Include
    @Column(name = "color", nullable = false, length = 7)
    @Setter
    private String color;

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
    @OneToMany(mappedBy = "habitCategory" ,fetch = FetchType.LAZY ,
            cascade = {CascadeType.DETACH, CascadeType.MERGE ,CascadeType.PERSIST ,CascadeType.REFRESH})
    private final List<Habit> habits = new ArrayList<>() ;

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "habitCategory" ,fetch = FetchType.LAZY ,
            cascade = {CascadeType.DETACH, CascadeType.MERGE ,CascadeType.PERSIST ,CascadeType.REFRESH})
    private final List<HabitTask> habitTasks = new ArrayList<>() ;

    // ===== Builder Constructor =====
    @Builder
    public HabitCategory(@NonNull String name, String description, String color) {
        this.name = name;
        this.description = description;
        this.color = (color != null && !color.trim().isEmpty()) ? color : generateRandomColor();
    }

    // ===== Helper Method for Color =====
    private String generateRandomColor() {
        return String.format("#%06x", ThreadLocalRandom.current().nextInt(0x1000000));
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
