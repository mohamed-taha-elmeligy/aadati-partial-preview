package com.mts.aadati.calculation;

import com.mts.aadati.entities.HabitCalendar;
import com.mts.aadati.entities.HabitCompletion;
import com.mts.aadati.entities.PercentageDay;
import com.mts.aadati.services.HabitCompletionService;
import com.mts.aadati.services.PercentageDayService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */
@AllArgsConstructor
@Component
@Slf4j
@Transactional
public class CalculationPercentageRateForDay {

    private final HabitCompletionService habitCompletionService;
    private final PercentageDayService percentageDayService;


    public void calculationPercentageRateForDay(@NonNull UUID userId, @NonNull HabitCalendar habitCalendar) {
        log.debug("calculationPercentageRateForDay called with userId={} , calendarId={}", userId, habitCalendar.getHabitCalendarId());

        List<HabitCompletion> habitCompletionInDayList = habitCompletionService.findByEntityHabitCalendarAndUser(userId, habitCalendar);
        if (habitCompletionInDayList.isEmpty()) {
            log.warn("No habit completions found for userId={} and calendarId={}", userId, habitCalendar.getHabitCalendarId());
            return;
        }

        double totalPoints = 0;
        double completedPoints = 0;

        for (HabitCompletion habitCompletion : habitCompletionInDayList) {
            double habitPoint = habitCompletion.getHabit().getPoint();
            totalPoints += habitPoint;
            if (habitCompletion.isCompleted()) {
                completedPoints += habitPoint;
            }
        }

        Optional<PercentageDay> percentageDayOp = percentageDayService.findEntityByDate(userId, habitCalendar.getDate());
        if (percentageDayOp.isEmpty()) {
            log.warn("No PercentageDay found for userId={} and date={}", userId, habitCalendar.getDate());
            return;
        }

        PercentageDay percentageDay = percentageDayOp.get();
        percentageDay.setRate(calculateRate(completedPoints, totalPoints));

        if (percentageDayService.updatePercentageDay(percentageDay) != null) {
            log.info("Successfully updated PercentageDay for date={}", habitCalendar.getDate());
        } else {
            log.debug("No changes made to PercentageDay for date={}", habitCalendar.getDate());
        }
    }


    private BigDecimal calculateRate(double completedPoints, double totalPoints) {
        if (totalPoints == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(completedPoints)
                .divide(BigDecimal.valueOf(totalPoints), 2, RoundingMode.HALF_UP);
    }
}
