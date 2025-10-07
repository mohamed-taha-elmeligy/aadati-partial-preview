
package com.mts.aadati.autogenerators;

import com.mts.aadati.entities.HabitCalendar;
import com.mts.aadati.entities.HabitWeek;
import com.mts.aadati.services.HabitWeekService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */
@Component
@AllArgsConstructor
public class DayAutoGenerator {

    private final HabitWeekService habitWeekService;

    public List<HabitCalendar> dayAutoGenerator(LocalDate startDate) {
        if (startDate == null) return Collections.emptyList();

        List<HabitCalendar> habitCalendars = new ArrayList<>();
        LocalDate lastDate = startDate;

        while (!lastDate.isAfter(LocalDate.now())) {
            Optional<HabitWeek> habitWeek = habitWeekService.findEntityByWeekNumberAndYear(
                    lastDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR),
                    lastDate.getYear()
            );

            LocalDate finalLastDate = lastDate;
            habitWeek.ifPresent(hw -> habitCalendars.add(buildHabitCalendar(finalLastDate, hw)));
            lastDate = lastDate.plusDays(1);
        }

        return habitCalendars;
    }

    private HabitCalendar buildHabitCalendar(LocalDate date, HabitWeek habitWeek) {
        return HabitCalendar.builder()
                .dayOfWeek(date.getDayOfWeek())
                .date(date)
                .habitWeek(habitWeek)
                .build();
    }


}
