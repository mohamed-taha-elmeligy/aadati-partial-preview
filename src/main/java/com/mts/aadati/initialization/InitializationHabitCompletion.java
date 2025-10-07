package com.mts.aadati.initialization;

import com.mts.aadati.entities.Habit;
import com.mts.aadati.entities.HabitCalendar;
import com.mts.aadati.entities.HabitCompletion;
import com.mts.aadati.entities.HabitDayWeek;
import com.mts.aadati.services.HabitCalendarService;
import com.mts.aadati.services.HabitCompletionService;
import com.mts.aadati.services.HabitDayWeekService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
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
public class InitializationHabitCompletion {

    private final HabitCalendarService habitCalendarService ;
    private final HabitDayWeekService habitDayWeekService ;
    private final HabitCompletionService habitCompletionService ;

    public void addHabitToCompletion() {
        log.debug("addHabitToCompletion called");

        Optional<HabitCalendar> habitCalendar = habitCalendarService.findEntityByDate(LocalDate.now());
        if (habitCalendar.isEmpty()) {
            log.error("addHabitToCompletion failed: habitCalendar not found for today");
            return;
        }

        List<Habit> habitList = habitDayWeekService.findHabitsByDayOfWeek(LocalDate.now().getDayOfWeek().toString());
        if (habitList.isEmpty()) {
            log.warn("addHabitToCompletion: no habits found for today");
            return;
        }

        List<HabitCompletion> habitCompletionList = new ArrayList<>();
        HabitCalendar calendar = habitCalendar.get();

        for (Habit habit : habitList) {
            if (!habitCompletionService.existsByHabitAndCalendar(habit.getHabitId(), calendar.getHabitCalendarId())) {
                habitCompletionList.add(HabitCompletion.builder()
                        .complete(false)
                        .habitCalendar(calendar)
                        .habit(habit)
                        .build());
            }
        }

        if (!habitCompletionList.isEmpty()) {
            boolean result = habitCompletionService.addAll(habitCompletionList);
            if (result) {
                log.info("HabitCompletion added successfully: {} items", habitCompletionList.size());
            } else {
                log.error("Failed to save HabitCompletion items");
            }
        } else {
            log.debug("No new HabitCompletion items to add");
        }
    }

    public void addLaterHabitToCompletion() {
        log.debug("addLaterHabitToCompletion called - filling missing habit completions");

        Optional<HabitCompletion> lastHabitCompletion = habitCompletionService.findFirstByOrderByCreatedAtDesc();
        if (lastHabitCompletion.isEmpty()) {
            log.warn("No previous habit completions found - calling regular addHabitToCompletion instead");
            addHabitToCompletion();
            return;
        }

        LocalDate startDate = LocalDate.ofInstant(
                lastHabitCompletion.get().getCreatedAt(),
                ZoneId.systemDefault()
        );

        LocalDate endDate = LocalDate.now();

        if (startDate.isAfter(endDate)) {
            log.debug("No missing days to process - data is up to date");
            return;
        }

        log.info("Processing missing habit completions from {} to {}", startDate, endDate);

        List<HabitCalendar> habitCalendars = habitCalendarService.findEntityByDateRange(startDate, endDate);
        if (habitCalendars.isEmpty()) {
            log.warn("No habit calendars found for date range {} to {}", startDate, endDate);
            return;
        }

        List<HabitCompletion> habitCompletionList = new ArrayList<>();
        int processedDays = 0;
        int totalItemsAdded = 0;

        for (HabitCalendar calendar : habitCalendars) {
            LocalDate currentDate = calendar.getDate();
            String dayOfWeek = currentDate.getDayOfWeek().toString();

            List<Habit> habitsForDay = habitDayWeekService.findHabitsByDayOfWeek(dayOfWeek);
            if (habitsForDay.isEmpty()) {
                log.debug("No habits found for {} ({})", currentDate, dayOfWeek);
                continue;
            }

            int itemsForThisDay = 0;

            for (Habit habit : habitsForDay) {
                if (!habitCompletionService.existsByHabitAndCalendar(habit.getHabitId(), calendar.getHabitCalendarId())) {
                    habitCompletionList.add(HabitCompletion.builder()
                            .complete(false)
                            .habitCalendar(calendar)
                            .habit(habit)
                            .build());
                    itemsForThisDay++;
                }
            }

            if (itemsForThisDay > 0) {
                processedDays++;
                totalItemsAdded += itemsForThisDay;
                log.debug("Added {} habit completions for {} ({})", itemsForThisDay, currentDate, dayOfWeek);
            }
        }

        if (!habitCompletionList.isEmpty()) {
            boolean result = habitCompletionService.addAll(habitCompletionList);
            if (result) {
                log.info("Successfully filled missing habit completions: {} items across {} days",
                        totalItemsAdded, processedDays);
            } else {
                log.error("Failed to save {} missing habit completion items", habitCompletionList.size());
            }
        } else {
            log.debug("No missing habit completions found - all data is up to date");
        }
    }


    public void addNewHabitToCompletion(Habit habit ) {
        log.debug("addNewHabitToCompletion called");

        Optional<HabitCalendar> habitCalendar = habitCalendarService.findEntityByDate(LocalDate.now());
        if (habitCalendar.isEmpty()) {
            log.error("addNewHabitToCompletion failed: habitCalendar not found for today");
            return;
        }

        if (!habitCompletionService.existsByHabitAndCalendar(habit.getHabitId(), habitCalendar.get().getHabitCalendarId())) {
            List<HabitDayWeek> habitDayWeekList = habit.getHabitDayWeeks();
            for (HabitDayWeek habitDayWeek : habitDayWeekList) {
                if (habitDayWeek.getDayOfWeek().equals(LocalDate.now().getDayOfWeek())) {
                    HabitCompletion habitCompletion = HabitCompletion.builder()
                            .complete(false)
                            .habitCalendar(habitCalendar.get())
                            .habit(habit)
                            .build();

                    Optional<HabitCompletion> result = habitCompletionService.create(habitCompletion);
                    if (result.isPresent()) {
                        log.info("a HabitCompletion added successfully: {} items", habitCompletion);
                    } else {
                        log.error("Failed to save a HabitCompletion items");
                    }
                    break;
                }
            }
        }
    }



}
