
package com.mts.aadati.autogenerators;

import com.mts.aadati.entities.HabitWeek;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.List;
/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */
@Component
public class WeekAutoGenerator {

    private WeekAutoGenerator() {
    }

    public static List<HabitWeek> weekAutoGenerator(LocalDate startWeek) {

        List<HabitWeek> weeks = new ArrayList<>();

        while (startWeek.getYear() == LocalDate.now().getYear()) {
            LocalDate endOfWeek = startWeek.plusDays(6);

            int isoWeekNumber = startWeek.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);

            weeks.add(
                    HabitWeek.builder()
                            .weekNumber(isoWeekNumber)
                            .startWeek(startWeek)
                            .endWeek(endOfWeek)
                            .year(startWeek.getYear())
                            .build()
            );
            startWeek = startWeek.plusWeeks(1);
        }
        return weeks;
    }
}
