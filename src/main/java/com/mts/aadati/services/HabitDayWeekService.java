package com.mts.aadati.services;

import com.mts.aadati.entities.Habit;
import com.mts.aadati.entities.HabitDayWeek;
import com.mts.aadati.repository.HabitDayWeekRepository;
import com.mts.aadati.dto.mapper.HabitDayWeekMapper;
import com.mts.aadati.dto.request.HabitDayWeekRequest;
import com.mts.aadati.dto.response.HabitDayWeekResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.*;

/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */
@Service
@AllArgsConstructor
public class HabitDayWeekService {

    private final HabitDayWeekRepository habitDayWeekRepository;

    private static final Logger logger = LoggerFactory.getLogger(HabitDayWeekService.class);

    // ===== CRUD =====

    public List<HabitDayWeekResponse> findAll() {
        logger.info("Fetching all HabitDayWeek entries");
        List<HabitDayWeek> list = habitDayWeekRepository.findByOrderByDayWeekIdAsc();
        return list.stream()
                .map(HabitDayWeekMapper::toResponse)
                .toList();
    }

    @Transactional
    public Optional<HabitDayWeekResponse> add(HabitDayWeekRequest request) {
        if (request == null) {
            logger.warn("add failed: request is null");
            return Optional.empty();
        }

        DayOfWeek dayOfWeek = request.getDayOfWeek();
        if (habitDayWeekRepository.findByDayOfWeek(dayOfWeek).isPresent()) {
            logger.warn("HabitDayWeek for {} already exists", dayOfWeek);
            return Optional.empty();
        }

        HabitDayWeek entity = HabitDayWeekMapper.toEntity(request);
        HabitDayWeek saved = habitDayWeekRepository.save(entity);
        logger.info("HabitDayWeek added successfully for {}", dayOfWeek);
        return Optional.of(HabitDayWeekMapper.toResponse(saved));
    }

    @Transactional
    public boolean addAll(List<HabitDayWeek> requests) {
        if (requests == null || requests.isEmpty()) {
            logger.warn("addAll failed: request list is empty or null");
            return false;
        }

        List<HabitDayWeek> toSave = new ArrayList<>();
        for (HabitDayWeek req : requests) {
            DayOfWeek day = req.getDayOfWeek();
            if (habitDayWeekRepository.findByDayOfWeek(day).isEmpty()) {
                toSave.add(req);
            } else {
                logger.info("Skipping existing day: {}", day);
            }
        }

        if (toSave.isEmpty()) {
            logger.info("No new HabitDayWeek entries to save");
            return false;
        }

        habitDayWeekRepository.saveAll(toSave);
        logger.info("Saved {} new HabitDayWeek entries", toSave.size());
        return true;
    }


    @Transactional
    public Optional<HabitDayWeekResponse> update(long id, HabitDayWeekRequest request) {
        if (request == null) {
            logger.warn("update failed: request is null");
            return Optional.empty();
        }

        Optional<HabitDayWeek> existingOpt = habitDayWeekRepository.findById(id);
        if (existingOpt.isEmpty()) {
            logger.warn("update failed: HabitDayWeek with id {} not found", id);
            return Optional.empty();
        }

        HabitDayWeek existing = existingOpt.get();
        existing.setDayOfWeek(request.getDayOfWeek());
        HabitDayWeek saved = habitDayWeekRepository.save(existing);
        logger.info("HabitDayWeek updated successfully with id {}", id);
        return Optional.of(HabitDayWeekMapper.toResponse(saved));
    }

    // ===== Habits =====

    public List<Habit> findHabitsByDayOfWeek(String dayOfWeek) {
        if (dayOfWeek == null || dayOfWeek.isBlank()) {
            logger.warn("findHabitsByDayOfWeek failed: dayOfWeek is null or blank");
            return Collections.emptyList();
        }
        List<Habit> habits = habitDayWeekRepository.findHabitsByDayOfWeek(dayOfWeek);
        logger.info("Found {} habits for day {}", habits.size(), dayOfWeek);
        return habits;
    }

    public long countHabitsByDayOfWeek(DayOfWeek day) {
        if (day == null) {
            logger.warn("countHabitsByDayOfWeek failed: day is null");
            return -1;
        }
        long count = habitDayWeekRepository.countHabitsByDayOfWeek(day);
        logger.info("Number of habits for {}: {}", day, count);
        return count;
    }

    // ===== Search by DayOfWeek Entity =====
    public Optional<HabitDayWeek> findByDayOfWeekEntity(DayOfWeek day) {
        if (day == null) {
            logger.warn("findByDayOfWeekEntity failed: day is null");
            return Optional.empty();
        }
        return habitDayWeekRepository.findByDayOfWeek(day);
    }

    public Optional<HabitDayWeekResponse> findByDayOfWeek(String dayOfWeek) {
        if (dayOfWeek == null || dayOfWeek.isBlank()) {
            logger.warn("findByDayOfWeek failed: dayOfWeek is null or blank");
            return Optional.empty();
        }
        try {
            DayOfWeek day = DayOfWeek.valueOf(dayOfWeek.toUpperCase());
            return habitDayWeekRepository.findByDayOfWeek(day)
                    .map(HabitDayWeekMapper::toResponse);
        } catch (IllegalArgumentException e) {
            logger.warn("findByDayOfWeek failed: invalid dayOfWeek '{}'", dayOfWeek);
            return Optional.empty();
        }
    }

}
