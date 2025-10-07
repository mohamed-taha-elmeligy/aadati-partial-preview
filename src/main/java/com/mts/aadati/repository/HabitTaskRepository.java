package com.mts.aadati.repository;

import com.mts.aadati.entities.*;
import com.mts.aadati.enums.RecurrenceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */
@Repository
public interface HabitTaskRepository extends JpaRepository<HabitTask, UUID> {

    // ===== Find =====
    Optional<HabitTask> findByUserAndHabitTaskIdAndIsActiveTrue(User user, UUID habitTaskId);
    // ===== Containing Search =====
    List<HabitTask> findByUserAndTitleContainingIgnoreCaseAndIsActiveTrue(User user, String title);

    // ===== Filter =====
    @Query("""
            SELECT ht FROM HabitTask ht WHERE ht.user = :user AND 
            ht.isActive = TRUE AND 
            (:recurrenceType IS NULL OR ht.recurrenceType = :recurrenceType) AND 
            (:priorityLevel IS NULL OR ht.taskPriorityLevel = :priorityLevel) AND 
            (:category IS NULL OR ht.habitCategory = :category)
           """)
    Page<HabitTask> filterTasks(@Param("user") User user,
                                @Param("category") HabitCategory habitCategory,
                                @Param("priorityLevel") TaskPriorityLevel taskPriorityLevel,
                                @Param("recurrenceType") RecurrenceType recurrenceType,
                                Pageable pageable);

    // ===== Find By Pageable =====
    Page<HabitTask> findAllByUserAndIsActiveTrue(User user, Pageable pageable);
    Page<HabitTask> findAllByUserAndIsActiveFalse(User user, Pageable pageable);
    Page<HabitTask> findByUserAndStartDateAndIsActiveTrue(User user, Instant startDate , Pageable pageable);
    Page<HabitTask> findByUserAndHabitCategoryAndIsActiveTrue(User user, HabitCategory habitCategory, Pageable pageable);
    Page<HabitTask> findByUserAndTaskPriorityLevelAndIsActiveTrue(User user, TaskPriorityLevel taskPriorityLevel, Pageable pageable);
    Page<HabitTask> findByUserAndRecurrenceTypeAndIsActiveTrue(User user, RecurrenceType recurrenceType, Pageable pageable);

    // ===== Exist =====
    boolean existsByUserAndHabitTaskIdAndIsActiveTrue(User user, UUID habitTaskId);
    boolean existsByUserAndTitleAndIsActiveTrue(User user, String title);

    // ===== Count =====
    long countByUserAndStartDateAndIsActiveTrue(User user, Instant startDate);
    long countByUserAndRecurrenceTypeAndIsActiveTrue(User user, RecurrenceType recurrenceType);
    long countByUserAndTaskPriorityLevelAndIsActiveTrue(User user, TaskPriorityLevel taskPriorityLevel);
    long countByUserAndHabitCategoryAndIsActiveTrue(User user, HabitCategory habitCategory);

}
