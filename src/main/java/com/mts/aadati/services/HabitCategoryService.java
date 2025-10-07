package com.mts.aadati.services;

import com.mts.aadati.dto.mapper.HabitCategoryMapper;
import com.mts.aadati.dto.request.HabitCategoryRequest;
import com.mts.aadati.dto.response.HabitCategoryResponse;
import com.mts.aadati.entities.HabitCategory;
import com.mts.aadati.repository.HabitCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HabitCategoryService {

    private final HabitCategoryRepository habitCategoryRepository;

    // ===== Get All =====
    public List<HabitCategoryResponse> findAllHabitCategory() {
        log.debug("findAllHabitCategory called");
        List<HabitCategoryResponse> result = habitCategoryRepository.findAll()
                .stream()
                .map(HabitCategoryMapper::toResponse)
                .toList();
        log.info("findAllHabitCategory returned {} records", result.size());
        return result;
    }

    // ===== Add All =====
    public boolean addAllHabitCategory(List<HabitCategoryRequest> habitCategories) {
        log.debug("addAllHabitCategory called with {} requests",
                habitCategories != null ? habitCategories.size() : 0);

        if (habitCategories == null || habitCategories.isEmpty()) {
            log.warn("addAllHabitCategory received empty or null list");
            return false;
        }

        List<HabitCategory> save = habitCategories.stream()
                .map(HabitCategoryMapper::toEntity)
                .map(cat -> habitCategoryRepository.findByName(cat.getName())
                        .map(existing -> {
                            existing.setColor(cat.getColor());
                            existing.setDescription(cat.getDescription());
                            return existing;
                        })
                        .orElse(cat))
                .toList();

        habitCategoryRepository.saveAll(save);
        log.info("addAllHabitCategory saved {} records", save.size());
        return true;
    }

    public boolean addEntityAllHabitCategory(List<HabitCategory> habitCategories) {
        log.debug("addEntityAllHabitCategory called with {} entities",
                habitCategories != null ? habitCategories.size() : 0);

        if (habitCategories == null || habitCategories.isEmpty()) {
            log.warn("addEntityAllHabitCategory received empty or null list");
            return false;
        }

        List<HabitCategory> save = habitCategories.stream()
                .map(cat -> habitCategoryRepository.findByName(cat.getName())
                        .map(existing -> {
                            existing.setColor(cat.getColor());
                            existing.setDescription(cat.getDescription());
                            return existing;
                        })
                        .orElse(cat))
                .toList();

        habitCategoryRepository.saveAll(save);
        log.info("addEntityAllHabitCategory saved {} records", save.size());
        return true;
    }


    // ===== Search by Id =====
    public Optional<HabitCategoryResponse> findById(UUID id) {
        log.debug("findById called with id={}", id);
        return habitCategoryRepository.findById(id)
                .map(cat -> {
                    log.info("findById found record id={}", id);
                    return HabitCategoryMapper.toResponse(cat);
                });
    }

    // ===== Search by Name =====
    public List<HabitCategoryResponse> findByName(String name) {
        log.debug("findByName called with name={}", name);
        if (name == null || name.isBlank()) {
            log.warn("findByName called with empty name");
            return Collections.emptyList();
        }
        List<HabitCategoryResponse> result = habitCategoryRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(HabitCategoryMapper::toResponse)
                .toList();
        log.info("findByName returned {} records", result.size());
        return result;
    }

    // ===== Search by Color =====
    public List<HabitCategoryResponse> findByColor(String color) {
        log.debug("findByColor called with color={}", color);
        if (color == null || color.isBlank()) {
            log.warn("findByColor called with empty color");
            return Collections.emptyList();
        }
        List<HabitCategoryResponse> result = habitCategoryRepository.findByColorContainingIgnoreCase(color)
                .stream()
                .map(HabitCategoryMapper::toResponse)
                .toList();
        log.info("findByColor returned {} records", result.size());
        return result;
    }

    // ===== Search by Dates =====
    public List<HabitCategoryResponse> findByCreatedAtBetween(Instant start, Instant end) {
        log.debug("findByCreatedAtBetween called with start={} end={}", start, end);
        if (start == null || end == null) {
            log.warn("findByCreatedAtBetween received null values");
            return Collections.emptyList();
        }
        List<HabitCategoryResponse> result = habitCategoryRepository.findByCreatedAtBetween(start, end)
                .stream()
                .map(HabitCategoryMapper::toResponse)
                .toList();
        log.info("findByCreatedAtBetween returned {} records", result.size());
        return result;
    }

    public List<HabitCategoryResponse> findByUpdatedAtBetween(Instant start, Instant end) {
        log.debug("findByUpdatedAtBetween called with start={} end={}", start, end);
        if (start == null || end == null) {
            log.warn("findByUpdatedAtBetween received null values");
            return Collections.emptyList();
        }
        List<HabitCategoryResponse> result = habitCategoryRepository.findByUpdatedAtBetween(start, end)
                .stream()
                .map(HabitCategoryMapper::toResponse)
                .toList();
        log.info("findByUpdatedAtBetween returned {} records", result.size());
        return result;
    }

    // ===== Sorting =====
    public List<HabitCategoryResponse> findAllByCreatedAtDesc() {
        log.debug("findAllByCreatedAtDesc called");
        List<HabitCategoryResponse> result = habitCategoryRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(HabitCategoryMapper::toResponse)
                .toList();
        log.info("findAllByCreatedAtDesc returned {} records", result.size());
        return result;
    }

    public List<HabitCategoryResponse> findAllByUpdatedAtDesc() {
        log.debug("findAllByUpdatedAtDesc called");
        List<HabitCategoryResponse> result = habitCategoryRepository.findAllByOrderByUpdatedAtDesc()
                .stream()
                .map(HabitCategoryMapper::toResponse)
                .toList();
        log.info("findAllByUpdatedAtDesc returned {} records", result.size());
        return result;
    }

    // ===== Top 10 =====
    public List<HabitCategoryResponse> findTop10ByCreatedAtDesc() {
        log.debug("findTop10ByCreatedAtDesc called");
        List<HabitCategoryResponse> result = habitCategoryRepository.findTop10ByOrderByCreatedAtDesc()
                .stream()
                .map(HabitCategoryMapper::toResponse)
                .toList();
        log.info("findTop10ByCreatedAtDesc returned {} records", result.size());
        return result;
    }

    // ===== Exists Check =====
    public boolean existsByName(String name) {
        log.debug("existsByName called with name={}", name);
        if (name == null || name.isBlank()) {
            log.warn("existsByName called with empty name");
            return false;
        }
        boolean exists = habitCategoryRepository.existsByName(name);
        log.info("existsByName result={} for name={}", exists, name);
        return exists;
    }
}
