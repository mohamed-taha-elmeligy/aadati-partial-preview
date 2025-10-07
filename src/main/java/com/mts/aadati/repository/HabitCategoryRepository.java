
package com.mts.aadati.repository;

import com.mts.aadati.entities.HabitCategory;
import org.springframework.data.jpa.repository.JpaRepository;
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
public interface HabitCategoryRepository extends JpaRepository<HabitCategory, UUID> {

    // Helper
    List<HabitCategory> findByNameContainingIgnoreCase(String name);
    List<HabitCategory> findByColorContainingIgnoreCase(String color);

    List<HabitCategory> findByUpdatedAtBetween(Instant start , Instant end);
    List<HabitCategory> findByCreatedAtBetween(Instant start , Instant end);
    List<HabitCategory> findAllByOrderByCreatedAtDesc();
    List<HabitCategory> findAllByOrderByUpdatedAtDesc();

    List<HabitCategory> findTop10ByOrderByCreatedAtDesc();

    boolean existsByName(String name);
    Optional<HabitCategory> findByName(String name);


}
