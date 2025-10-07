
package com.mts.aadati.repository;

import com.mts.aadati.entities.Habit;
import com.mts.aadati.entities.HabitCategory;
import com.mts.aadati.entities.HabitDayWeek;
import com.mts.aadati.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */

@Repository
public interface HabitRepository extends JpaRepository<Habit, UUID> {

    // ===== Find =====
    Optional<Habit> findByHabitIdAndUserAndIsActiveTrue(UUID habitId, User user);
    List<Habit> findByHabitDayWeeks(HabitDayWeek habitDayWeek);
    // ===== Containing Search =====
    List<Habit> findByTitleContainingIgnoreCaseAndUserAndIsActiveTrue(String title, User user);

    // ===== Filter with Pageable =====
    @Query("""
            SELECT h FROM Habit h WHERE h.user = :user AND 
            h.isActive = TRUE AND 
            (:category IS NULL OR h.habitCategory = :category) AND 
            (:dayWeek IS NULL OR :dayWeek MEMBER OF h.habitDayWeeks) AND
            (:type IS NULL OR h.type = :type)
           """)
    Page<Habit> filterHabits(@Param("user") User user,
                             @Param("category") HabitCategory category,
                             @Param("dayWeek") HabitDayWeek dayWeek,
                             @Param("type") Boolean type,
                             Pageable pageable);

    // ===== Find By Pageable =====
    Page<Habit> findAllByUserAndIsActiveTrue(User user, Pageable pageable);
    Page<Habit> findAllByUserAndIsActiveFalse(User user, Pageable pageable);
    Page<Habit> findByUserAndHabitCategoryAndIsActiveTrue(User user, HabitCategory habitCategory, Pageable pageable);
    Page<Habit> findByUserAndHabitDayWeeksContainingAndIsActiveTrue(User user, HabitDayWeek habitDayWeek, Pageable pageable);
    Page<Habit> findByUserAndTypeAndIsActiveTrue(User user, boolean type ,Pageable pageable);
    Page<Habit> findByUserAndPointAndIsActiveTrue(User user, double point ,Pageable pageable);
    Optional<Habit> findByHabitIdAndUser(UUID habitId, User user);


    // ===== Exists =====
    boolean existsByTitleAndUserAndIsActiveTrue(String title, User user);
    boolean existsByHabitIdAndUserAndIsActiveTrue(UUID habitId, User user);

    // ===== Count =====
    long countByUserAndIsActiveTrue(User user);
    long countByUserAndPointLessThanAndIsActiveTrue(User user, double point);
    long countByUserAndPointGreaterThanAndIsActiveTrue(User user, double point);
    long countByUserAndTypeAndIsActiveTrue(User user, boolean type);
    long countByUserAndHabitCategoryAndIsActiveTrue(User user, HabitCategory category);
    long countByUserAndHabitDayWeeksContainingAndIsActiveTrue(User user, HabitDayWeek habitDayWeek);

}
