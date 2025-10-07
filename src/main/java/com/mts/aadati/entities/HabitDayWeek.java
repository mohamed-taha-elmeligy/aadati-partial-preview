
package com.mts.aadati.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */

@NoArgsConstructor
@Getter  @ToString @EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity @Table(name = "habit_day_week" , indexes = @Index(name = "inx_habit_day_week",columnList = "day_of_week"))
public class HabitDayWeek {

    @Id
    @ToString.Include
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "day_week_id" , nullable = false , updatable = false)
    private long dayWeekId ;

    @ToString.Include
    @Column(name = "day_of_week" , nullable = false ,length = 10 ,unique = true)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Day of week is required")
    @Setter
    private DayOfWeek dayOfWeek ;

    // =====  Relationship =====
    @JsonIgnore
    @ToString.Exclude
    @ManyToMany(mappedBy = "habitDayWeeks",fetch = FetchType.LAZY)
    private final List<Habit> habits =new ArrayList<>();


    // ===== Builder Constructor =====

    @Builder
    public HabitDayWeek (@NonNull DayOfWeek dayOfWeek){
        this.dayOfWeek = dayOfWeek ;
    }

}
