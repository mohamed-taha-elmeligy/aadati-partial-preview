package com.mts.aadati.controllers;

import com.mts.aadati.dto.request.HabitRequest;
import com.mts.aadati.dto.response.HabitResponse;
import com.mts.aadati.security.CustomUserDetails;
import com.mts.aadati.services.HabitService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */

@RestController
@AllArgsConstructor
@RequestMapping("/habit")
public class HabitController {

    private static final String MESSAGE = "message" ;
    private final HabitService habitService;
    private static final Logger log = LoggerFactory.getLogger(HabitController.class);

    // ===== CRUD =====
    @PostMapping("/add")
    public ResponseEntity<?> addHabit(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody HabitRequest request) {
        return habitService.addHabit(userDetails.getId(), request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().body((HabitResponse)Map.of(MESSAGE, "Failed to add habit")));
    }

    @PutMapping("/update/{habitId}")
    public ResponseEntity<?> updateHabit(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID habitId,
            @Valid @RequestBody HabitRequest request) {
        return habitService.updateHabit(userDetails.getId(), habitId, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().body((HabitResponse) Map.of(MESSAGE, "Failed to update habit")));
    }

    @DeleteMapping("/delete/{habitId}")
    public ResponseEntity<?> deleteHabit(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID habitId) {
        if (habitService.deleteHabit(userDetails.getId(), habitId)) {
            return ResponseEntity.ok(Map.of(MESSAGE, "Habit deleted successfully"));
        }
        return ResponseEntity.status(404).body(Map.of(MESSAGE, "Habit not found or already inactive"));
    }

    @DeleteMapping("/permanent-delete/{habitId}")
    public ResponseEntity<?> permanentDeleteHabit(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID habitId) {
        if (habitService.permanentDeleteHabit(userDetails.getId(), habitId)) {
            return ResponseEntity.ok(Map.of(MESSAGE, "Habit permanently deleted"));
        }
        return ResponseEntity.status(404).body(Map.of(MESSAGE, "Habit not found"));
    }

    // ===== Find =====
    @GetMapping("/find-id/{habitId}")
    public ResponseEntity<?> findById(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID habitId) {
        return habitService.findById(userDetails.getId(), habitId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ===== Search =====
    @GetMapping("/search-title")
    public ResponseEntity<?> searchByTitle(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam
            @NotBlank(message = "Title must not be empty")
            @Size(min = 2, max = 50, message = "Title length must be between 2 and 50 characters")
            @Pattern(regexp = "^[\\p{L}\\p{N} ]+$", message = "Title can only contain letters, numbers, and spaces")
            String title) {
        List<HabitResponse> habits = habitService.searchByTitle(userDetails.getId(), title);
        if (habits.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(habits);
    }

    // ===== Pageable =====
    @GetMapping("/pageable-type")
    public ResponseEntity<Page<HabitResponse>> pageableByType(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam boolean type,
            @RequestParam @PositiveOrZero int pageNumber,
            @RequestParam @Positive int pageSize) {
        Page<HabitResponse> page = habitService.pageableByType(userDetails.getId(), type, pageNumber, pageSize);
        return page.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(page);
    }

    @GetMapping("/pageable-point")
    public ResponseEntity<Page<HabitResponse>> pageableByPoint(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam @DecimalMin(value = "0.0", message = "Point must be greater than or equal to 0") double point,
            @RequestParam @PositiveOrZero int pageNumber,
            @RequestParam @Positive int pageSize) {
        Page<HabitResponse> page = habitService.pageableByPoint(userDetails.getId(), point, pageNumber, pageSize);
        return page.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(page);
    }

    @GetMapping("/pageable-all-active")
    public ResponseEntity<Page<HabitResponse>> pageableAllActive(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam @PositiveOrZero int pageNumber,
            @RequestParam @Positive int pageSize) {
        Page<HabitResponse> page = habitService.pageableAllActive(userDetails.getId(), pageNumber, pageSize);
        return page.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(page);
    }

    @GetMapping("/pageable-all-inactive")
    public ResponseEntity<Page<HabitResponse>> pageableAllInactive(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam @PositiveOrZero int pageNumber,
            @RequestParam @Positive int pageSize) {
        Page<HabitResponse> page = habitService.pageableAllInactive(userDetails.getId(), pageNumber, pageSize);
        return page.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(page);
    }

    @GetMapping("/pageable-category")
    public ResponseEntity<Page<HabitResponse>> pageableByCategory(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam @NotNull UUID categoryId,
            @RequestParam @PositiveOrZero int pageNumber,
            @RequestParam @Positive int pageSize) {
        Page<HabitResponse> page = habitService.pageableByCategory(userDetails.getId(), categoryId, pageNumber, pageSize);
        return page.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(page);
    }

    @GetMapping("/pageable-day")
    public ResponseEntity<Page<HabitResponse>> pageableByDayOfWeek(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam @Positive long habitDayWeekId,
            @RequestParam @PositiveOrZero int pageNumber,
            @RequestParam @Positive int pageSize) {
        Page<HabitResponse> page = habitService.pageableByDayOfWeek(userDetails.getId(), habitDayWeekId, pageNumber, pageSize);
        return page.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(page);
    }

    @GetMapping("/pageable-filter")
    public ResponseEntity<Page<HabitResponse>> filterHabits(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) Long habitDayWeekId,
            @RequestParam(required = false) Boolean type,
            @RequestParam @PositiveOrZero int pageNumber,
            @RequestParam @Positive int pageSize) {
        Page<HabitResponse> page = habitService.filterHabits(userDetails.getId(), categoryId, habitDayWeekId, type, pageNumber, pageSize);
        return page.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(page);
    }

    // ===== Exists =====
    @GetMapping("/exist-id/{habitId}")
    public ResponseEntity<Map<String, Boolean>> existsById(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID habitId) {
        boolean exists = habitService.existsById(userDetails.getId(), habitId);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    @GetMapping("/exist-title")
    public ResponseEntity<Map<String, Boolean>> existsByTitle(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam
            @NotBlank(message = "Title must not be empty")
            @Size(min = 2, max = 50, message = "Title length must be between 2 and 50 characters")
            @Pattern(regexp = "^[\\p{L}\\p{N} ]+$", message = "Title can only contain letters, numbers, and spaces")
            String title) {
        boolean exists = habitService.existsByTitle(userDetails.getId(), title);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    // ===== Count =====
    @GetMapping("/count-all")
    public long countByUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return habitService.countByUser(userDetails.getId());
    }

    @GetMapping("/count-point-less")
    public long countByPointLessThan(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam @DecimalMin(value = "0.0", message = "Point must be greater than or equal to 0") double point) {
        return habitService.countByPointLessThan(userDetails.getId(), point);
    }

    @GetMapping("/count-point-greater")
    public long countByPointGreaterThan(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam @DecimalMin(value = "0.0", message = "Point must be greater than or equal to 0") double point) {
        return habitService.countByPointGreaterThan(userDetails.getId(), point);
    }

    @GetMapping("/count-type")
    public long countByType(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam boolean type) {
        return habitService.countByType(userDetails.getId(), type);
    }

    @GetMapping("/count-category")
    public long countByCategory(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam @NotNull UUID categoryId) {
        return habitService.countByCategory(userDetails.getId(), categoryId);
    }

    @GetMapping("/count-day")
    public long countByHabitDayWeek(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam @Positive long habitDayWeekId) {
        return habitService.countByHabitDayWeek(userDetails.getId(), habitDayWeekId);
    }
}
