
package com.mts.aadati.calculation;

import com.mts.aadati.entities.*;
import com.mts.aadati.services.HabitWeekService;
import com.mts.aadati.services.PercentageDayService;
import com.mts.aadati.services.PercentageWeekService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
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
public class CalculationPercentageRateForWeek {

    private final PercentageDayService percentageDayService ;
    private final PercentageWeekService percentageWeekService ;
    private final HabitWeekService habitWeekService ;

    public void calculationPercentageRateForWeek(@NonNull UUID userId, @NonNull LocalDate localDate){

        log.debug("calculationPercentageRateForWeek called with : userId = {} , localDate = {}", userId,localDate);

        Optional<HabitWeek> habitWeekOptional =
                habitWeekService.findEntityByWeekNumberAndYear(localDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR),localDate.getYear() );
        if (habitWeekOptional.isEmpty()) {
            log.warn("No habitWeekOptional found for  localDate {}",localDate);
            return;
        }

        List<PercentageDay> percentageDayList =percentageDayService.findByEntityUserAndWeek(userId,habitWeekOptional.get());
        if (percentageDayList.isEmpty()) {
            log.warn("No percentageDayList found for userId {} and localDate {}", userId,localDate);
            return;
        }

        BigDecimal pointCompleted = BigDecimal.ZERO;
        for (PercentageDay percentageDay : percentageDayList) {
            pointCompleted = pointCompleted.add(percentageDay.getRate());
        }


        Optional<PercentageWeek> percentageWeekOptional = percentageWeekService.findEntityByDate(userId,localDate);
        if (percentageWeekOptional.isEmpty()) {
            log.warn("No percentageWeekOptional found for userId {} and localDate {}", userId,localDate);
            return;
        }

        PercentageWeek percentageWeek = percentageWeekOptional.get();
        percentageWeek.setRate(calculateRate(percentageDayList.size(),pointCompleted));


        if (percentageWeekService.updatePercentageWeek(percentageWeek) != null)
            log.info("Successfully updated PercentageWeek for week number {} of year {}", habitWeekOptional.get().getWeekNumber(), habitWeekOptional.get().getYear());
        else {
            log.debug("No missing percentageDay completions found ");
        }

    }


    private BigDecimal calculateRate(int numberOfDay ,BigDecimal pointOfCompletedHabit) {
        return pointOfCompletedHabit
                .divide(BigDecimal.valueOf(100.00* numberOfDay), 2, RoundingMode.HALF_UP);
    }

}
