package com.mts.aadati.services;

import com.mts.aadati.dto.mapper.HabitCompletionMapper;
import com.mts.aadati.dto.response.HabitCompletionResponse;
import com.mts.aadati.entities.*;
import com.mts.aadati.repository.HabitCompletionRepository;
import com.mts.aadati.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */
@Service
@AllArgsConstructor
public class HabitCompletionService {

    private static final String SORT_COMPLETED_AT = "completedAt";

    private final HabitCompletionRepository habitCompletionRepository;
    private final UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(HabitCompletionService.class);

    // ===== Helper Methods =====
    private Pageable pageable(int pageNumber, int pageSize, String sortBy) {
        int size = pageSize > 0 ? pageSize : 10;
        return PageRequest.of(pageNumber, size, Sort.by(Sort.Direction.DESC, sortBy));
    }

    private Optional<User> getExistingUser(UUID userId) {
        log.debug("getExistingUser called with userId: {}", userId);
        if (userId == null) {
            log.warn("getExistingUser failed: userId is null");
            return Optional.empty();
        }
        return userRepository.findById(userId);
    }

    // ===== Create =====
    public Optional<HabitCompletion> create(HabitCompletion habitCompletion) {
        log.debug("create called");

        if (habitCompletion == null) {
            log.warn("create failed: habitCompletion entity is null");
            return Optional.empty();
        }

        try {
            HabitCompletion saved = habitCompletionRepository.save(habitCompletion);
            log.info("HabitCompletion created: {}", saved.getHabitCompletionId());
            return Optional.of(saved);
        } catch (Exception e) {
            log.error("Failed to save habitCompletion: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public boolean addAll(List<HabitCompletion> habitCompletionList) {
        log.debug("addAll called with {} items", habitCompletionList.size());

        if (habitCompletionList.isEmpty()) {
            log.warn("addAll called with empty list");
            return true;
        }

        try {
            List<HabitCompletion> saved = habitCompletionRepository.saveAll(habitCompletionList);
            log.info("Successfully saved {} habitCompletion items", saved.size());
            return true;
        } catch (Exception e) {
            log.error("Failed to save habitCompletion items: {}", e.getMessage());
            return false;
        }
    }

    public boolean existsByHabitAndCalendar(UUID habitId, UUID calendarId) {
        log.debug("existsByHabitAndCalendar called with habitId: {}, calendarId: {}", habitId, calendarId);

        if (habitId == null || calendarId == null) {
            log.warn("existsByHabitAndCalendar called with null parameters");
            return false;
        }

        return habitCompletionRepository.existsByHabitHabitIdAndHabitCalendarHabitCalendarId(habitId, calendarId);
    }



    // ===== Update status =====
    public Optional<HabitCompletionResponse> updateStatus(UUID userId, UUID completionId, boolean complete) {
        return getExistingUser(userId).flatMap(user -> {
            if (completionId == null) {
                log.warn("updateStatus failed: invalid completionId");
                return Optional.empty();
            }
            return habitCompletionRepository.findByIdAndUser(completionId, user).map(completion -> {
                if (complete) {
                    completion.markComplete();
                } else {
                    completion.markIncomplete();
                }
                HabitCompletion updated = habitCompletionRepository.save(completion);
                log.info("HabitCompletion status updated: {}", updated.getHabitCompletionId());
                return HabitCompletionMapper.toResponse(updated);
            });
        });
    }

    // ===== Find =====
    public Optional<HabitCompletionResponse> findByIdAndUser(UUID userId, UUID completionId) {
        return getExistingUser(userId).flatMap(user -> {
            if (completionId == null) {
                log.warn("findByIdAndUser failed: invalid completionId");
                return Optional.empty();
            }
            return habitCompletionRepository.findByIdAndUser(completionId, user)
                    .map(HabitCompletionMapper::toResponse);
        });
    }
    public Optional<HabitCompletion> findFirstByOrderByCreatedAtDesc() {
        log.debug("findFirstByOrderByCreatedAtDesc called ");
        return habitCompletionRepository.findFirstByOrderByCreatedAtDesc();
    }

    public List<HabitCompletionResponse> findByHabitAndUser(UUID userId, Habit habit) {
        return getExistingUser(userId).map(user -> {
            if (habit == null) {
                return Collections.<HabitCompletionResponse>emptyList();
            }
            return habitCompletionRepository.findByHabitAndUser(habit, user).stream()
                    .map(HabitCompletionMapper::toResponse)
                    .toList();
        }).orElse(Collections.emptyList());
    }

    public List<HabitCompletion> findByEntityHabitCalendarAndUser(UUID userId, HabitCalendar habitCalendar) {
        if (habitCalendar == null) {
            return Collections.emptyList();
        }
        return getExistingUser(userId)
                .map(user -> habitCompletionRepository.findByHabitCalendarAndUser(habitCalendar, user))
                .orElse(Collections.emptyList());
    }



    public List<HabitCompletionResponse> findCompleted(UUID userId, Habit habit) {
        return getExistingUser(userId).map(user -> {
            if (habit == null) {
                return Collections.<HabitCompletionResponse>emptyList();
            }
            return habitCompletionRepository.findCompletedByHabitAndUser(habit, user).stream()
                    .map(HabitCompletionMapper::toResponse)
                    .toList();
        }).orElse(Collections.emptyList());
    }

    public List<HabitCompletionResponse> findUncompleted(UUID userId, Habit habit) {
        return getExistingUser(userId).map(user -> {
            if (habit == null) {
                return Collections.<HabitCompletionResponse>emptyList();
            }
            return habitCompletionRepository.findUncompletedByHabitAndUser(habit, user).stream()
                    .map(HabitCompletionMapper::toResponse)
                    .toList();
        }).orElse(Collections.emptyList());
    }

    // ===== Find By Date =====
    public List<HabitCompletionResponse> findByDateRange(UUID userId, Instant start, Instant end) {
        return getExistingUser(userId).map(user -> {
            if (start == null || end == null) {
                return Collections.<HabitCompletionResponse>emptyList();
            }
            return habitCompletionRepository.findByUserAndCompletedAtBetween(user, start, end).stream()
                    .map(HabitCompletionMapper::toResponse)
                    .toList();
        }).orElse(Collections.emptyList());
    }

    // ===== Count =====
    public long countCompleted(UUID userId, Habit habit) {
        return getExistingUser(userId).map(user -> {
            if (habit == null) {
                return -1L;
            }
            return habitCompletionRepository.countCompletedForHabit(habit, user);
        }).orElse(-1L);
    }

    public long countUncompleted(UUID userId, Habit habit) {
        return getExistingUser(userId).map(user -> {
            if (habit == null) {
                return -1L;
            }
            return habitCompletionRepository.countUncompletedForHabit(habit, user);
        }).orElse(-1L);
    }

    public long countCompletedByCalendar(UUID userId, HabitCalendar habitCalendar) {
        return getExistingUser(userId).map(user -> {
            if (habitCalendar == null) {
                return -1L;
            }
            return habitCompletionRepository.countCompletedForCalendar(habitCalendar, user);
        }).orElse(-1L);
    }

    public long countUncompletedCalendar(UUID userId, HabitCalendar habitCalendar) {
        return getExistingUser(userId).map(user -> {
            if (habitCalendar == null) {
                return -1L;
            }
            return habitCompletionRepository.countUncompletedForCalendar(habitCalendar, user);
        }).orElse(-1L);
    }

    // ===== Pageable =====
    public Page<HabitCompletionResponse> pageableAll(UUID userId, int pageNumber, int pageSize) {
        return getExistingUser(userId).map(user ->
                habitCompletionRepository.findAllByUser(user, pageable(pageNumber, pageSize, SORT_COMPLETED_AT))
                        .map(HabitCompletionMapper::toResponse)
        ).orElse(Page.empty());
    }

    public Page<HabitCompletionResponse> findTodayCompletedByUser(UUID userId, int pageNumber, int pageSize) {
        return getExistingUser(userId).map(user ->
                habitCompletionRepository.findTodayCompletedByUser(user, pageable(pageNumber, pageSize, SORT_COMPLETED_AT))
                        .map(HabitCompletionMapper::toResponse)
        ).orElse(Page.empty());
    }

    public Page<HabitCompletionResponse> findTodayUncompletedByUser(UUID userId, int pageNumber, int pageSize) {
        return getExistingUser(userId).map(user ->
                habitCompletionRepository.findTodayUncompletedByUser(user, pageable(pageNumber, pageSize, SORT_COMPLETED_AT))
                        .map(HabitCompletionMapper::toResponse)
        ).orElse(Page.empty());
    }

    public Page<HabitCompletionResponse> pageableSearchByTitle(UUID userId, String title, int pageNumber, int pageSize) {
        return getExistingUser(userId).map(user -> {
            if (title == null || title.isBlank()) {
                return Page.<HabitCompletionResponse>empty();
            }
            return habitCompletionRepository.findByHabitTitleContainingAndUser(title, user,
                            pageable(pageNumber, pageSize, SORT_COMPLETED_AT))
                    .map(HabitCompletionMapper::toResponse);
        }).orElse(Page.empty());
    }

    // ===== Search (non-pageable) =====
    public List<HabitCompletionResponse> searchByTitle(UUID userId, String title) {
        return getExistingUser(userId).map(user -> {
            if (title == null || title.isBlank()) {
                return Collections.<HabitCompletionResponse>emptyList();
            }
            return habitCompletionRepository.findByHabitTitleContainingAndUser(title, user).stream()
                    .map(HabitCompletionMapper::toResponse)
                    .toList();
        }).orElse(Collections.emptyList());
    }

    public Page<HabitCompletionResponse> pageableTodayByUser(UUID userId, int pageNumber, int pageSize) {
        return getExistingUser(userId).map(user ->
                habitCompletionRepository.findTodayByUser(user, pageable(pageNumber, pageSize, SORT_COMPLETED_AT))
                        .map(HabitCompletionMapper::toResponse)
        ).orElse(Page.empty());
    }

}
