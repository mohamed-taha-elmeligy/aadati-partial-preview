package com.mts.aadati.controllers;

import com.mts.aadati.calculation.CalculationPercentageRateForDay;
import com.mts.aadati.calculation.CalculationPercentageRateForWeek;
import com.mts.aadati.dto.response.HabitCompletionResponse;
import com.mts.aadati.entities.Habit;
import com.mts.aadati.entities.HabitCalendar;
import com.mts.aadati.security.CustomUserDetails;
import com.mts.aadati.services.HabitCalendarService;
import com.mts.aadati.services.HabitCompletionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */
@RestController
@RequestMapping("/api/v1/habit-completions")
@RequiredArgsConstructor
public class HabitCompletionController {

    private static final Logger log = LoggerFactory.getLogger(HabitCompletionController.class);

    private final HabitCompletionService habitCompletionService;
    private final CalculationPercentageRateForDay calculationPercentageRateForDay ;
    private final CalculationPercentageRateForWeek calculationPercentageRateForWeek ;
    private final HabitCalendarService habitCalendarService ;

    private HabitCalendar resolveCalendarByDate(UUID calendarId) {
        log.debug("Resolving HabitCalendar for calendarId={}", calendarId);
        return habitCalendarService.findEntityById(calendarId)
                .orElseThrow(() -> {
                    log.error("HabitCalendar not found for calendarId={}", calendarId);
                    return new NoSuchElementException("HabitCalendar not found for calendarId=" + calendarId);
                });
    }

    // ===== Update Status =====
    @Transactional
    @PutMapping("/{completionId}/status")
    public ResponseEntity<HabitCompletionResponse> updateStatus(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                @PathVariable UUID completionId,
                                                                @RequestParam boolean complete) {
        log.debug("PUT /api/v1/habit-completions/{}/status called by user {}", completionId, userDetails.getId());
        Optional<HabitCompletionResponse> response =
                habitCompletionService.updateStatus(userDetails.getId(), completionId, complete);

        return response.map(r -> {
            HabitCalendar calendar = resolveCalendarByDate(r.getHabitCalendarId());

            calculationPercentageRateForDay.calculationPercentageRateForDay(userDetails.getId(), calendar);
            calculationPercentageRateForWeek.calculationPercentageRateForWeek(userDetails.getId(), calendar.getDate());

            return ResponseEntity.ok(r);
        }).orElseGet(() -> ResponseEntity.notFound().build());

    }

    // ===== Find By Id =====
    @GetMapping("/{completionId}")
    public ResponseEntity<HabitCompletionResponse> findById(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                            @PathVariable UUID completionId) {
        log.debug("GET /api/v1/habit-completions/{} called by user {}", completionId, userDetails.getId());
        Optional<HabitCompletionResponse> response =
                habitCompletionService.findByIdAndUser(userDetails.getId(), completionId);
        return response.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ===== Find Completed By Habit =====
    @GetMapping("/habit/{habitId}/completed")
    public ResponseEntity<List<HabitCompletionResponse>> findCompleted(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                       @PathVariable UUID habitId) {
        log.debug("GET /api/v1/habit-completions/habit/{}/completed called by user {}", habitId, userDetails.getId());
        Habit habit = new Habit();
        habit.setHabitId(habitId);
        List<HabitCompletionResponse> results = habitCompletionService.findCompleted(userDetails.getId(), habit);
        return ResponseEntity.ok(results);
    }

    // ===== Find Uncompleted By Habit =====
    @GetMapping("/habit/{habitId}/uncompleted")
    public ResponseEntity<List<HabitCompletionResponse>> findUncompleted(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                         @PathVariable UUID habitId) {
        log.debug("GET /api/v1/habit-completions/habit/{}/uncompleted called by user {}", habitId, userDetails.getId());
        Habit habit = new Habit();
        habit.setHabitId(habitId);
        List<HabitCompletionResponse> results = habitCompletionService.findUncompleted(userDetails.getId(), habit);
        return ResponseEntity.ok(results);
    }

    // ===== Find By Date Range =====
    @GetMapping("/date-range")
    public ResponseEntity<List<HabitCompletionResponse>> findByDateRange(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                         @RequestParam Instant start,
                                                                         @RequestParam Instant end) {
        log.debug("GET /api/v1/habit-completions/date-range called by user {}", userDetails.getId());
        List<HabitCompletionResponse> results = habitCompletionService.findByDateRange(userDetails.getId(), start, end);
        return ResponseEntity.ok(results);
    }

    // ===== Count Completed =====
    @GetMapping("/habit/{habitId}/count/completed")
    public ResponseEntity<Long> countCompleted(@AuthenticationPrincipal CustomUserDetails userDetails,
                                               @PathVariable UUID habitId) {
        log.debug("GET /api/v1/habit-completions/habit/{}/count/completed countCompleted called by user {}", habitId, userDetails.getId());
        Habit habit = new Habit();
        habit.setHabitId(habitId);
        long count = habitCompletionService.countCompleted(userDetails.getId(), habit);
        return ResponseEntity.ok(count);
    }

    // ===== Count Uncompleted =====
    @GetMapping("/habit/{habitId}/count/uncompleted")
    public ResponseEntity<Long> countUncompleted(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                 @PathVariable UUID habitId) {
        log.debug("GET /api/v1/habit-completions/habit/{}/count/uncompleted countUncompleted called by user {}", habitId, userDetails.getId());
        Habit habit = new Habit();
        habit.setHabitId(habitId);
        long count = habitCompletionService.countUncompleted(userDetails.getId(), habit);
        return ResponseEntity.ok(count);
    }

    // ===== Pageable All =====
    @GetMapping("/page")
    public ResponseEntity<Page<HabitCompletionResponse>> pageableAll(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                     @RequestParam int page,
                                                                     @RequestParam int size) {
        log.debug("GET /api/v1/habit-completions/page called by user {}", userDetails.getId());
        Page<HabitCompletionResponse> results = habitCompletionService.pageableAll(userDetails.getId(), page, size);
        return ResponseEntity.ok(results);
    }

    // ===== Pageable Completed =====
    @GetMapping("/page/completed/today")
    public ResponseEntity<Page<HabitCompletionResponse>> pageableCompleted(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                           @RequestParam int page,
                                                                           @RequestParam int size) {
        log.debug("GET /api/v1/habit-completions/page/completed called by user {}", userDetails.getId());
        Page<HabitCompletionResponse> results = habitCompletionService.findTodayCompletedByUser(userDetails.getId(), page, size);
        return ResponseEntity.ok(results);
    }

    // ===== Pageable Uncompleted =====
    @GetMapping("/page/uncompleted/today")
    public ResponseEntity<Page<HabitCompletionResponse>> pageableUncompleted(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                             @RequestParam int page,
                                                                             @RequestParam int size) {
        log.debug("GET /api/v1/habit-completions/page/uncompleted called by user {}", userDetails.getId());
        Page<HabitCompletionResponse> results = habitCompletionService.findTodayUncompletedByUser(userDetails.getId(), page, size);
        return ResponseEntity.ok(results);
    }

    // ===== Search By Title (Pageable) =====
    @GetMapping("/page/search")
    public ResponseEntity<Page<HabitCompletionResponse>> pageableSearchByTitle(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                               @RequestParam String title,
                                                                               @RequestParam int page,
                                                                               @RequestParam int size) {
        log.debug("GET /api/v1/habit-completions/page/search called by user {}", userDetails.getId());
        Page<HabitCompletionResponse> results =
                habitCompletionService.pageableSearchByTitle(userDetails.getId(), title, page, size);
        return ResponseEntity.ok(results);
    }

    // ===== Search By Title (Non-Pageable) =====
    @GetMapping("/search")
    public ResponseEntity<List<HabitCompletionResponse>> searchByTitle(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                       @RequestParam String title) {
        log.debug("GET /api/v1/habit-completions/search called by user {}", userDetails.getId());
        List<HabitCompletionResponse> results = habitCompletionService.searchByTitle(userDetails.getId(), title);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/page/today")
    public ResponseEntity<Page<HabitCompletionResponse>> pageableToday(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                       @RequestParam int page,
                                                                       @RequestParam int size) {
        log.debug("GET /api/v1/habit-completions/page/today called by user {}", userDetails.getId());

        Page<HabitCompletionResponse> results = habitCompletionService
                .pageableTodayByUser(userDetails.getId(), page, size);

        return ResponseEntity.ok(results);
    }

}
