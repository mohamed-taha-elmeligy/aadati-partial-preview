package com.mts.aadati.initialization;

import com.mts.aadati.entities.*;
import com.mts.aadati.services.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */

@AllArgsConstructor
@Transactional
@Component
@Slf4j
public class InitializationPercentageWeek {


    private final UserService userService ;
    private final PercentageWeekService percentageWeekService ;
    private final HabitWeekService habitWeekService ;

    public void initializationWeek (){
        log.debug("initializationWeek called");
        List<PercentageWeek> percentageWeekList = new ArrayList<>();

        LocalDate date = LocalDate.now() ;

        Optional<HabitWeek> habitWeekNow =
                habitWeekService.findEntityByWeekNumberAndYear(
                        date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR),date.getYear());
        if (habitWeekNow.isEmpty()) {
            log.error("initializationWeek failed: habitWeek not found for today");
            return;
        }

        List<User> userList = userService.getEntityAllUsers();
        if (userList.isEmpty()) {
            log.warn("initializationWeek: no users found");
            return;
        }

        HabitWeek habitWeek = habitWeekNow.get();

        for(User user : userList){

            if (!percentageWeekService.existsByUserAndWeek(user.getUserId(), habitWeek)) {
                percentageWeekList.add(PercentageWeek.builder()
                        .habitWeek(habitWeek)
                        .rate(BigDecimal.ZERO)
                        .user(user)
                        .build());
            }
        }

        if (!percentageWeekList.isEmpty()) {
            if(percentageWeekService.addAllPercentageWeek(percentageWeekList))
                log.info("initializationWeek Successful - Added {} records", percentageWeekList.size());
            else
                log.error("initializationWeek failed");
        } else {
            log.debug("No new PercentageWeek records to add");
        }
    }

    public void initializationMulti (){
        log.debug("initializationMulti called");

        Optional<PercentageWeek> lastPercentageWeek = percentageWeekService.findFirstByOrderByCreatedAtDesc();

        if (lastPercentageWeek.isEmpty()) {
            log.warn("No previous PercentageWeek found - calling initializationWeek instead");
            initializationWeek();
            return;
        }

        LocalDate startDate = LocalDate.ofInstant(
                lastPercentageWeek.get().getCreatedAt(),
                ZoneId.systemDefault()
        );
        LocalDate endDate = LocalDate.now();

        if (startDate.isAfter(endDate)) {
            log.debug("No missing weeks to process - data is up to date");
            return;
        }

        log.info("Processing missing PercentageWeek from {} to {}", startDate, endDate);

        List<HabitWeek> habitWeekList = habitWeekService.findEntityByStartWeekBetween(startDate, endDate);
        if (habitWeekList.isEmpty()) {
            log.warn("No HabitWeek found for date range {} to {}", startDate, endDate);
            return;
        }

        List<User> userList = userService.getEntityAllUsers();
        if (userList.isEmpty()) {
            log.warn("initializationMulti: no users found");
            return;
        }

        List<PercentageWeek> percentageWeekList = new ArrayList<>();

        for (HabitWeek habitWeek : habitWeekList) {
            for(User user : userList){
                if (!percentageWeekService.existsByUserAndWeek(user.getUserId(), habitWeek)) {
                    percentageWeekList.add(PercentageWeek.builder()
                            .habitWeek(habitWeek)
                            .rate(BigDecimal.ZERO)
                            .user(user)
                            .build());
                }
            }
        }

        if (!percentageWeekList.isEmpty()) {
            boolean result = percentageWeekService.addAllPercentageWeek(percentageWeekList);
            if (result) {
                log.info("Successfully filled {} missing PercentageWeek records", percentageWeekList.size());
            } else {
                log.error("Failed to save missing PercentageWeek items");
            }
        } else {
            log.debug("No missing PercentageWeek found - all data is up to date");
        }
    }
}
