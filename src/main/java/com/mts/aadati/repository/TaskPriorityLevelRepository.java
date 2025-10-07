package com.mts.aadati.repository;

import com.mts.aadati.entities.TaskPriorityLevel;
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
public interface TaskPriorityLevelRepository extends JpaRepository<TaskPriorityLevel, UUID> {

    Optional<TaskPriorityLevel> searchByPriorityLevel(Integer priorityLevel);

    @Query("SELECT t.color from TaskPriorityLevel t")
    List<String> getAllColor();

    List<TaskPriorityLevel> findAllByOrderByPriorityLevelAsc();
    List<TaskPriorityLevel> findAllByOrderByPriorityLevelDesc();

    @Query("SELECT t FROM TaskPriorityLevel t " +
            "WHERE (:priorityLevel IS NULL OR t.priorityLevel = :priorityLevel) " +
            "OR (:color IS NULL OR LOWER(t.color) = LOWER(:color))")
    List<TaskPriorityLevel> searchByPriorityLevelOrColor(
            @Param("priorityLevel") Integer priorityLevel,
            @Param("color") String color
    );
    boolean existsByPriorityLevel(Integer priorityLevel);
    Optional<TaskPriorityLevel> findByPriorityLevel(Integer priorityLevel);
}

