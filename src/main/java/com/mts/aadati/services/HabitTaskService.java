package com.mts.aadati.services;

import com.mts.aadati.dto.mapper.HabitTaskMapper;
import com.mts.aadati.dto.request.HabitTaskRequest;
import com.mts.aadati.dto.response.HabitTaskResponse;
import com.mts.aadati.entities.*;
import com.mts.aadati.enums.RecurrenceType;
import com.mts.aadati.initialization.InitializationTaskCompletion;
import com.mts.aadati.repository.HabitTaskRepository;
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
public class HabitTaskService {
    private final HabitTaskRepository habitTaskRepository;
    private final UserRepository userRepository;
    private final InitializationTaskCompletion initializationTaskCompletion ;

    private static final Logger log = LoggerFactory.getLogger(HabitTaskService.class);
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final String TITLE = "title";

    // ===== Helper Methods =====
    private Optional<User> getUser(UUID userId) {
        if (userId == null) {
            log.warn("getUser: userId is null");
            return Optional.empty();
        }
        return userRepository.findById(userId);
    }

    private Pageable pageable(int pageNumber, int pageSize, String sortBy) {
        int finalPageSize = (pageSize <= 0) ? DEFAULT_PAGE_SIZE : pageSize;
        return PageRequest.of(pageNumber, finalPageSize, Sort.by(Sort.Direction.ASC, sortBy));
    }

    // ===== CRUD =====
    public Optional<HabitTaskResponse> addHabitTask(UUID userId, HabitTaskRequest request,
                                                    TaskPriorityLevel taskPriorityLevel,
                                                    HabitCategory habitCategory) {
        return getUser(userId).map(user -> {
            HabitTask habitTask = HabitTaskMapper.toEntity(request, user, taskPriorityLevel, habitCategory);
            HabitTask saved = habitTaskRepository.save(habitTask);
            initializationTaskCompletion.addNewTaskToCompletion(saved);
            log.info("HabitTask added successfully: {}", habitTask.getTitle());
            return HabitTaskMapper.toResponse(habitTask);
        });
    }

    public Optional<HabitTaskResponse> updateHabitTask(UUID userId, UUID habitTaskId, HabitTaskRequest request,
                                                       TaskPriorityLevel taskPriorityLevel,
                                                       HabitCategory habitCategory) {
        return getUser(userId).flatMap(user ->
                habitTaskRepository.findByUserAndHabitTaskIdAndIsActiveTrue(user, habitTaskId)
                        .map(existing -> {
                            HabitTask updated = HabitTaskMapper.toEntity(request, user, taskPriorityLevel, habitCategory);
                            HabitTask saved = habitTaskRepository.save(updated);
                            initializationTaskCompletion.addNewTaskToCompletion(saved);
                            log.info("HabitTask updated successfully: {}", updated.getTitle());
                            return HabitTaskMapper.toResponse(updated);
                        })
        );
    }

    public boolean deleteHabitTask(UUID userId, UUID habitTaskId) {
        return getUser(userId).flatMap(user ->
                habitTaskRepository.findByUserAndHabitTaskIdAndIsActiveTrue(user, habitTaskId)
        ).map(task -> {
            task.deactivate();
            habitTaskRepository.save(task);
            log.info("HabitTask deactivated successfully: {}", task.getTitle());
            return true;
        }).orElse(false);
    }

    // ===== Find =====
    public Optional<HabitTaskResponse> findById(UUID userId, UUID habitTaskId) {
        return getUser(userId).flatMap(user ->
                habitTaskRepository.findByUserAndHabitTaskIdAndIsActiveTrue(user, habitTaskId)
                        .map(HabitTaskMapper::toResponse)
        );
    }

    // ===== Containing Search =====
    public List<HabitTaskResponse> searchByTitle(UUID userId, String title) {
        if (title == null || title.isBlank()) {
            log.warn("searchByTitle: title is invalid");
            return Collections.emptyList();
        }
        return getUser(userId)
                .map(user -> habitTaskRepository.findByUserAndTitleContainingIgnoreCaseAndIsActiveTrue(user, title))
                .orElse(Collections.emptyList())
                .stream().map(HabitTaskMapper::toResponse).toList();
    }

    // ===== Find By Pageable =====
    public Page<HabitTaskResponse> pageableAllActive(UUID userId, int pageNumber, int pageSize) {
        return getUser(userId)
                .map(user -> habitTaskRepository.findAllByUserAndIsActiveTrue(user, pageable(pageNumber, pageSize, TITLE))
                        .map(HabitTaskMapper::toResponse))
                .orElse(Page.empty());
    }

    public Page<HabitTaskResponse> pageableAllInactive(UUID userId, int pageNumber, int pageSize) {
        return getUser(userId)
                .map(user -> habitTaskRepository.findAllByUserAndIsActiveFalse(user, pageable(pageNumber, pageSize, TITLE))
                        .map(HabitTaskMapper::toResponse))
                .orElse(Page.empty());
    }

    public Page<HabitTaskResponse> pageableByStartDate(UUID userId, Instant startDate, int pageNumber, int pageSize) {
        if (startDate == null) {
            log.warn("pageableByStartDate: startDate is null");
            return Page.empty();
        }
        return getUser(userId)
                .map(user -> habitTaskRepository.findByUserAndStartDateAndIsActiveTrue(user, startDate,
                                PageRequest.of(pageNumber, pageSize <= 0 ? DEFAULT_PAGE_SIZE : pageSize,
                                        Sort.by(Sort.Direction.DESC, "startDate")))
                        .map(HabitTaskMapper::toResponse))
                .orElse(Page.empty());
    }

    public Page<HabitTaskResponse> pageableByCategory(UUID userId, HabitCategory category, int pageNumber, int pageSize) {
        if (category == null) {
            log.warn("pageableByCategory: category is null");
            return Page.empty();
        }
        return getUser(userId)
                .map(user -> habitTaskRepository.findByUserAndHabitCategoryAndIsActiveTrue(user, category,
                                pageable(pageNumber, pageSize, "habitCategory"))
                        .map(HabitTaskMapper::toResponse))
                .orElse(Page.empty());
    }


    public Page<HabitTaskResponse> pageableByPriorityLevel(UUID userId, TaskPriorityLevel priority, int pageNumber, int pageSize) {
        if (priority == null) {
            log.warn("pageableByPriorityLevel: priority is null");
            return Page.empty();
        }
        return getUser(userId)
                .map(user -> habitTaskRepository.findByUserAndTaskPriorityLevelAndIsActiveTrue(user, priority,
                                pageable(pageNumber, pageSize, "taskPriorityLevel"))
                        .map(HabitTaskMapper::toResponse))
                .orElse(Page.empty());
    }

    public Page<HabitTaskResponse> pageableByRecurrenceType(UUID userId, RecurrenceType recurrenceType, int pageNumber, int pageSize) {
        if (recurrenceType == null) {
            log.warn("pageableByRecurrenceType: recurrenceType is null");
            return Page.empty();
        }
        return getUser(userId)
                .map(user -> habitTaskRepository.findByUserAndRecurrenceTypeAndIsActiveTrue(user, recurrenceType,
                                pageable(pageNumber, pageSize, "recurrenceType"))
                        .map(HabitTaskMapper::toResponse))
                .orElse(Page.empty());
    }

    public Page<HabitTaskResponse> filterTasks(UUID userId,
                                               HabitCategory habitCategory,
                                               TaskPriorityLevel taskPriorityLevel,
                                               RecurrenceType recurrenceType,
                                               int pageNumber, int pageSize) {
        return getUser(userId)
                .map(user -> habitTaskRepository.filterTasks(user, habitCategory, taskPriorityLevel, recurrenceType,
                                pageable(pageNumber, pageSize, TITLE))
                        .map(HabitTaskMapper::toResponse))
                .orElse(Page.empty());
    }

    // ===== Exists =====
    public boolean existsById(UUID userId, UUID habitTaskId) {
        return getUser(userId).map(user ->
                habitTaskRepository.existsByUserAndHabitTaskIdAndIsActiveTrue(user, habitTaskId)
        ).orElse(false);
    }

    public boolean existsByTitle(UUID userId, String title) {
        if (title == null || title.isBlank()) {
            log.warn("existsByTitle: title is invalid");
            return false;
        }
        return getUser(userId).map(user ->
                habitTaskRepository.existsByUserAndTitleAndIsActiveTrue(user, title)
        ).orElse(false);
    }

    public Optional<HabitTask> findActiveByUserAndTaskId(UUID userId, UUID habitTaskId) {
        log.debug("Fetching active HabitTask for userId={} and taskId={}", userId, habitTaskId);

        return getUser(userId)
                .flatMap(user -> habitTaskRepository.findByUserAndHabitTaskIdAndIsActiveTrue(user, habitTaskId))
                .map(task -> {
                    log.debug("Found active HabitTask with id={} for userId={}", habitTaskId, userId);
                    return task;
                });
    }

    // ===== Count =====
    public long countByStartDate(UUID userId, Instant startDate) {
        if (startDate == null) {
            log.warn("countByStartDate: startDate is null");
            return 0L;
        }
        return getUser(userId).map(user ->
                habitTaskRepository.countByUserAndStartDateAndIsActiveTrue(user, startDate)
        ).orElse(0L);
    }

    public long countByRecurrenceType(UUID userId, RecurrenceType recurrenceType) {
        if (recurrenceType == null) {
            log.warn("countByRecurrenceType: recurrenceType is null");
            return 0L;
        }
        return getUser(userId).map(user ->
                habitTaskRepository.countByUserAndRecurrenceTypeAndIsActiveTrue(user, recurrenceType)
        ).orElse(0L);
    }

    public long countByTaskPriorityLevel(UUID userId, TaskPriorityLevel priority) {
        if (priority == null) {
            log.warn("countByTaskPriorityLevel: priority is null");
            return 0L;
        }
        return getUser(userId).map(user ->
                habitTaskRepository.countByUserAndTaskPriorityLevelAndIsActiveTrue(user, priority)
        ).orElse(0L);
    }

    public long countByCategory(UUID userId, HabitCategory category) {
        if (category == null) {
            log.warn("countByCategory: category is null");
            return 0L;
        }
        return getUser(userId).map(user ->
                habitTaskRepository.countByUserAndHabitCategoryAndIsActiveTrue(user, category)
        ).orElse(0L);
    }
}
