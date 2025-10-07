package com.mts.aadati.controllers;

import com.mts.aadati.dto.request.HabitTaskRequest;
import com.mts.aadati.dto.response.HabitTaskResponse;
import com.mts.aadati.entities.HabitCategory;
import com.mts.aadati.entities.TaskPriorityLevel;
import com.mts.aadati.enums.RecurrenceType;
import com.mts.aadati.security.CustomUserDetails;
import com.mts.aadati.services.HabitTaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;

/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */

@RestController
@AllArgsConstructor
@RequestMapping("/habit-task")
public class HabitTaskController {

    private final HabitTaskService habitTaskService;
    private static final Logger log = LoggerFactory.getLogger(HabitTaskController.class);

    // ===== CRUD =====
    @PostMapping("/add")
    public ResponseEntity<?> addHabitTask(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody HabitTaskRequest request,
            @RequestParam @NotNull TaskPriorityLevel priority,
            @RequestParam @NotNull HabitCategory category) {
        return habitTaskService.addHabitTask(userDetails.getId(), request, priority, category)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().body((HabitTaskResponse) Map.of("message", "Failed to add habit task")));
    }

    @PutMapping("/update/{habitTaskId}")
    public ResponseEntity<?> updateHabitTask(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID habitTaskId,
            @Valid @RequestBody HabitTaskRequest request,
            @RequestParam @NotNull TaskPriorityLevel priority,
            @RequestParam @NotNull HabitCategory category) {
        return habitTaskService.updateHabitTask(userDetails.getId(), habitTaskId, request, priority, category)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().body((HabitTaskResponse) Map.of("message", "Failed to update habit task")));
    }

    @DeleteMapping("/delete/{habitTaskId}")
    public ResponseEntity<?> deleteHabitTask(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID habitTaskId) {
        if (habitTaskService.deleteHabitTask(userDetails.getId(), habitTaskId)) {
            return ResponseEntity.ok(Map.of("message", "Habit task deactivated successfully"));
        }
        return ResponseEntity.status(404).body(Map.of("message", "Habit task not found or already inactive"));
    }

    // ===== Find =====
    @GetMapping("/find-id/{habitTaskId}")
    public ResponseEntity<?> findById(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID habitTaskId) {
        return habitTaskService.findById(userDetails.getId(), habitTaskId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ===== Search =====
    @GetMapping("/search-title")
    public ResponseEntity<?> searchByTitle(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam
            @NotBlank(message = "Title must not be empty")
            @Size(min = 2, max = 100, message = "Title length must be between 2 and 100 characters")
            String title) {
        List<HabitTaskResponse> tasks = habitTaskService.searchByTitle(userDetails.getId(), title);
        if (tasks.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(tasks);
    }

    // ===== Pageable =====
    @GetMapping("/pageable-all-active")
    public ResponseEntity<Page<HabitTaskResponse>> pageableAllActive(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam @PositiveOrZero int pageNumber,
            @RequestParam @Positive int pageSize) {
        Page<HabitTaskResponse> page = habitTaskService.pageableAllActive(userDetails.getId(), pageNumber, pageSize);
        return page.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(page);
    }

    @GetMapping("/pageable-all-inactive")
    public ResponseEntity<Page<HabitTaskResponse>> pageableAllInactive(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam @PositiveOrZero int pageNumber,
            @RequestParam @Positive int pageSize) {
        Page<HabitTaskResponse> page = habitTaskService.pageableAllInactive(userDetails.getId(), pageNumber, pageSize);
        return page.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(page);
    }

    @GetMapping("/pageable-start-date")
    public ResponseEntity<Page<HabitTaskResponse>> pageableByStartDate(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam @NotNull Instant startDate,
            @RequestParam @PositiveOrZero int pageNumber,
            @RequestParam @Positive int pageSize) {
        Page<HabitTaskResponse> page = habitTaskService.pageableByStartDate(userDetails.getId(), startDate, pageNumber, pageSize);
        return page.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(page);
    }

    @GetMapping("/pageable-category")
    public ResponseEntity<Page<HabitTaskResponse>> pageableByCategory(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam @NotNull HabitCategory category,
            @RequestParam @PositiveOrZero int pageNumber,
            @RequestParam @Positive int pageSize) {
        Page<HabitTaskResponse> page = habitTaskService.pageableByCategory(userDetails.getId(), category, pageNumber, pageSize);
        return page.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(page);
    }

    @GetMapping("/pageable-priority")
    public ResponseEntity<Page<HabitTaskResponse>> pageableByPriority(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam @NotNull TaskPriorityLevel priority,
            @RequestParam @PositiveOrZero int pageNumber,
            @RequestParam @Positive int pageSize) {
        Page<HabitTaskResponse> page = habitTaskService.pageableByPriorityLevel(userDetails.getId(), priority, pageNumber, pageSize);
        return page.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(page);
    }

    @GetMapping("/pageable-recurrence")
    public ResponseEntity<Page<HabitTaskResponse>> pageableByRecurrence(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam @NotNull RecurrenceType recurrenceType,
            @RequestParam @PositiveOrZero int pageNumber,
            @RequestParam @Positive int pageSize) {
        Page<HabitTaskResponse> page = habitTaskService.pageableByRecurrenceType(userDetails.getId(), recurrenceType, pageNumber, pageSize);
        return page.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(page);
    }

    @GetMapping("/pageable-filter")
    public ResponseEntity<Page<HabitTaskResponse>> filterTasks(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) HabitCategory category,
            @RequestParam(required = false) TaskPriorityLevel priority,
            @RequestParam(required = false) RecurrenceType recurrenceType,
            @RequestParam @PositiveOrZero int pageNumber,
            @RequestParam @Positive int pageSize) {
        Page<HabitTaskResponse> page = habitTaskService.filterTasks(userDetails.getId(), category, priority, recurrenceType, pageNumber, pageSize);
        return page.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(page);
    }

    // ===== Exists =====
    @GetMapping("/exist-id/{habitTaskId}")
    public ResponseEntity<?> existsById(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID habitTaskId) {
        boolean exists = habitTaskService.existsById(userDetails.getId(), habitTaskId);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    @GetMapping("/exist-title")
    public ResponseEntity<?> existsByTitle(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam
            @NotBlank(message = "Title must not be empty")
            @Size(min = 2, max = 100, message = "Title length must be between 2 and 100 characters")
            String title) {
        boolean exists = habitTaskService.existsByTitle(userDetails.getId(), title);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    // ===== Count =====
    @GetMapping("/count-start-date")
    public long countByStartDate(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam @NotNull Instant startDate) {
        return habitTaskService.countByStartDate(userDetails.getId(), startDate);
    }

    @GetMapping("/count-recurrence")
    public long countByRecurrence(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam @NotNull RecurrenceType recurrenceType) {
        return habitTaskService.countByRecurrenceType(userDetails.getId(), recurrenceType);
    }

    @GetMapping("/count-priority")
    public long countByPriority(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam @NotNull TaskPriorityLevel priority) {
        return habitTaskService.countByTaskPriorityLevel(userDetails.getId(), priority);
    }

    @GetMapping("/count-category")
    public long countByCategory(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam @NotNull HabitCategory category) {
        return habitTaskService.countByCategory(userDetails.getId(), category);
    }
}
