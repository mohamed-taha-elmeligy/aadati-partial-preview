package com.mts.aadati.controllers;

import com.mts.aadati.dto.request.HabitCategoryRequest;
import com.mts.aadati.dto.response.HabitCategoryResponse;
import com.mts.aadati.services.HabitCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */

@RestController
@RequestMapping("/aadati/v1/habit-category")
@RequiredArgsConstructor
@Slf4j
public class HabitCategoryController {

    private final HabitCategoryService habitCategoryService;

    // ===== Get All =====
    @GetMapping("/all")
    public ResponseEntity<List<HabitCategoryResponse>> getAll() {
        log.debug(" GET /habit-category/all called");
        List<HabitCategoryResponse> result = habitCategoryService.findAllHabitCategory();
        log.info(" Returning {} habit categories", result.size());
        return ResponseEntity.ok(result);
    }

    // ===== Get By Id =====
    @GetMapping("/{id}")
    public ResponseEntity<HabitCategoryResponse> getById(@PathVariable UUID id) {
        log.debug(" GET /habit-category/{} called", id);
        Optional<HabitCategoryResponse> result = habitCategoryService.findById(id);
        return result.map(res -> {
            log.info("Found category with id={}", id);
            return ResponseEntity.ok(res);
        }).orElseGet(() -> {
            log.warn("Category with id={} not found", id);
            return ResponseEntity.notFound().build();
        });
    }

    // ===== Add All =====
    @PostMapping("/add-all")
    public ResponseEntity<Boolean> addAll(@RequestBody List<HabitCategoryRequest> requests) {
        log.debug(" POST /habit-category/add-all called with {} items",
                requests != null ? requests.size() : 0);
        boolean success = habitCategoryService.addAllHabitCategory(requests);
        if (success) {
            log.info("addAll saved {} categories", requests.size());
            return ResponseEntity.ok(true);
        } else {
            log.error("addAll failed to save categories");
            return ResponseEntity.badRequest().body(false);
        }
    }

    // ===== Search by Name =====
    @GetMapping("/search/name/{name}")
    public ResponseEntity<List<HabitCategoryResponse>> searchByName(@PathVariable String name) {
        log.debug("GET /habit-category/search/name/{} called", name);
        List<HabitCategoryResponse> result = habitCategoryService.findByName(name);
        log.info("searchByName returned {} records", result.size());
        return ResponseEntity.ok(result);
    }

    // ===== Search by Color =====
    @GetMapping("/search/color/{color}")
    public ResponseEntity<List<HabitCategoryResponse>> searchByColor(@PathVariable String color) {
        log.debug("GET /habit-category/search/color/{} called", color);
        List<HabitCategoryResponse> result = habitCategoryService.findByColor(color);
        log.info("searchByColor returned {} records", result.size());
        return ResponseEntity.ok(result);
    }

    // ===== Search by Created Date Range =====
    @GetMapping("/search/created")
    public ResponseEntity<List<HabitCategoryResponse>> searchByCreatedAt(
            @RequestParam Instant start,
            @RequestParam Instant end) {
        log.debug("GET /habit-category/search/created called with start={} end={}", start, end);
        List<HabitCategoryResponse> result = habitCategoryService.findByCreatedAtBetween(start, end);
        log.info("searchByCreatedAt returned {} records", result.size());
        return ResponseEntity.ok(result);
    }

    // ===== Search by Updated Date Range =====
    @GetMapping("/search/updated")
    public ResponseEntity<List<HabitCategoryResponse>> searchByUpdatedAt(
            @RequestParam Instant start,
            @RequestParam Instant end) {
        log.debug("GET /habit-category/search/updated called with start={} end={}", start, end);
        List<HabitCategoryResponse> result = habitCategoryService.findByUpdatedAtBetween(start, end);
        log.info("searchByUpdatedAt returned {} records", result.size());
        return ResponseEntity.ok(result);
    }

    // ===== Get All Sorted =====
    @GetMapping("/sort/created")
    public ResponseEntity<List<HabitCategoryResponse>> getAllByCreatedAtDesc() {
        log.debug("GET /habit-category/sort/created called");
        List<HabitCategoryResponse> result = habitCategoryService.findAllByCreatedAtDesc();
        log.info("getAllByCreatedAtDesc returned {} records", result.size());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/sort/updated")
    public ResponseEntity<List<HabitCategoryResponse>> getAllByUpdatedAtDesc() {
        log.debug("GET /habit-category/sort/updated called");
        List<HabitCategoryResponse> result = habitCategoryService.findAllByUpdatedAtDesc();
        log.info("getAllByUpdatedAtDesc returned {} records", result.size());
        return ResponseEntity.ok(result);
    }

    // ===== Top 10 =====
    @GetMapping("/top10")
    public ResponseEntity<List<HabitCategoryResponse>> getTop10() {
        log.debug("GET /habit-category/top10 called");
        List<HabitCategoryResponse> result = habitCategoryService.findTop10ByCreatedAtDesc();
        log.info("getTop10 returned {} records", result.size());
        return ResponseEntity.ok(result);
    }

    // ===== Exists Check =====
    @GetMapping("/exists/{name}")
    public ResponseEntity<Boolean> existsByName(@PathVariable String name) {
        log.debug("GET /habit-category/exists/{} called", name);
        boolean exists = habitCategoryService.existsByName(name);
        log.info("existsByName for '{}' result={}", name, exists);
        return ResponseEntity.ok(exists);
    }
}
