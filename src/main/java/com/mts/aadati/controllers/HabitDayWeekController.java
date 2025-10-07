package com.mts.aadati.controllers;

import com.mts.aadati.entities.Habit;
import com.mts.aadati.dto.response.HabitDayWeekResponse;
import com.mts.aadati.services.HabitDayWeekService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */
@RestController
@RequestMapping("/aadati/v1/day-of-week")
@RequiredArgsConstructor
public class HabitDayWeekController {

    private final HabitDayWeekService habitDayWeekService;
    private static final Logger logger = LoggerFactory.getLogger(HabitDayWeekController.class);

    // ====== CRUD ======
    @GetMapping("/all")
    public ResponseEntity<List<HabitDayWeekResponse>> getAll() {
        logger.info("GET /all called");
        List<HabitDayWeekResponse> list = habitDayWeekService.findAll();
        logger.info("Returning {} HabitDayWeek entries", list.size());
        return ResponseEntity.ok(list);
    }

    // ====== HABITS ======
    @GetMapping("/habits/{dayOfWeek}")
    public ResponseEntity<List<Habit>> getHabitsByDay(@PathVariable String dayOfWeek) {
        logger.info("GET /habits/{} called", dayOfWeek);
        if (dayOfWeek == null || dayOfWeek.isBlank()) {
            logger.warn("Bad request: dayOfWeek is null or blank");
            return ResponseEntity.badRequest().build();
        }
        List<Habit> habits = habitDayWeekService.findHabitsByDayOfWeek(dayOfWeek);
        logger.info("Found {} habits for day {}", habits.size(), dayOfWeek);
        return ResponseEntity.ok(habits);
    }

    @GetMapping("/habits/count/{dayOfWeek}")
    public ResponseEntity<Long> countHabitsByDay(@PathVariable String dayOfWeek) {
        logger.info("GET /habits/count/{} called", dayOfWeek);
        try {
            DayOfWeek day = DayOfWeek.valueOf(dayOfWeek.toUpperCase());
            long count = habitDayWeekService.countHabitsByDayOfWeek(day);
            logger.info("Number of habits for {}: {}", day, count);
            return ResponseEntity.ok(count);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid dayOfWeek '{}'", dayOfWeek);
            return ResponseEntity.badRequest().build();
        }
    }


    // ====== SINGLE DAY ======
    @GetMapping("/{dayOfWeek}")
    public ResponseEntity<HabitDayWeekResponse> getDayOfWeek(@PathVariable String dayOfWeek) {
        logger.info("GET /{} called", dayOfWeek);
        Optional<HabitDayWeekResponse> response = habitDayWeekService.findByDayOfWeek(dayOfWeek);
        return response.map(ResponseEntity::ok)
                .orElseGet(() -> {
                    logger.warn("DayOfWeek '{}' not found", dayOfWeek);
                    return ResponseEntity.notFound().build();
                });
    }
}
