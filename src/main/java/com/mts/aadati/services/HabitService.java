package com.mts.aadati.services;

import com.mts.aadati.dto.request.HabitRequest;
import com.mts.aadati.dto.response.HabitResponse;
import com.mts.aadati.entities.Habit;
import com.mts.aadati.entities.HabitCategory;
import com.mts.aadati.entities.HabitDayWeek;
import com.mts.aadati.entities.User;
import com.mts.aadati.dto.mapper.HabitMapper;
import com.mts.aadati.initialization.InitializationHabitCompletion;
import com.mts.aadati.repository.HabitCategoryRepository;
import com.mts.aadati.repository.HabitDayWeekRepository;
import com.mts.aadati.repository.HabitRepository;

import com.mts.aadati.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */
@Service
@AllArgsConstructor
public class HabitService {

    private static final String TITLE = "title";

    private final InitializationHabitCompletion initializationHabitCompletion ;
    private final HabitRepository habitRepository;
    private final HabitCategoryRepository habitCategoryRepository;
    private final HabitDayWeekRepository habitDayWeekRepository;
    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(HabitService.class);

    // ===== Helper Methods =====
    private Optional<User> getExistingUser(UUID userId) {
        logger.debug("getExistingUser called with userId: {}", userId);
        if (userId == null) {
            logger.warn("getExistingUser failed: userId is null");
            return Optional.empty();
        }
        return userRepository.findById(userId);
    }


    private Optional<HabitCategory> getExistingCategory(UUID categoryId) {
        logger.debug("getExistingCategory called with categoryId: {}", categoryId);
        if (categoryId == null) {
            logger.warn("getExistingCategory failed: categoryId is null");
            return Optional.empty();
        }
        return habitCategoryRepository.findById(categoryId);
    }

    private Optional<HabitDayWeek> getExistingHabitDayWeek(long habitDayWeekId) {
        logger.debug("getExistingHabitDayWeek called with habitDayWeekId: {}", habitDayWeekId);
        return habitDayWeekRepository.findById(habitDayWeekId);
    }

    private Pageable createPageable(int pageNumber, int pageSize, String sortBy) {
        int size = pageSize > 0 ? pageSize : 10;
        int page = Math.max(pageNumber, 0);
        return PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortBy));
    }

    // ===== Helper Methods CRUD =====

    @Transactional
    public Optional<HabitResponse> addHabit(UUID userId, HabitRequest request) {
        logger.debug("addHabit called for userId: {} with title: {}", userId,
                request != null ? request.getTitle() : "null");

        if (request == null) {
            logger.warn("addHabit failed: request is null");
            return Optional.empty();
        }

        Optional<User> userOpt = getExistingUser(userId);
        if (userOpt.isEmpty()) {
            logger.warn("addHabit failed: user not found with id: {}", userId);
            throw new NoSuchElementException("User not found");
        }

        Optional<HabitCategory> categoryOpt = getExistingCategory(request.getHabitCategoryId());
        if (categoryOpt.isEmpty()) {
            logger.warn("addHabit failed: category not found with id: {}", request.getHabitCategoryId());
            throw new NoSuchElementException("Category not found");
        }

        if (existsByTitle(userId, request.getTitle())) {
            logger.warn("addHabit failed: habit with title '{}' already exists for user: {}", request.getTitle(), userId);
            throw new IllegalArgumentException("Habit with this title already exists");
        }

        Habit habit = HabitMapper.toEntity(request, userOpt.get(), categoryOpt.get());

        if (request.getHabitDayWeekIds() != null && !request.getHabitDayWeekIds().isEmpty()) {
            List<HabitDayWeek> dayWeeks = habitDayWeekRepository.findAllById(request.getHabitDayWeekIds());
            if (dayWeeks.size() != request.getHabitDayWeekIds().size()) {
                logger.warn("addHabit failed: some day week IDs were not found");
                throw new IllegalArgumentException("Some day of week IDs were not found");
            }
            habit.getHabitDayWeeks().addAll(dayWeeks);
        }

        Habit saved = habitRepository.save(habit);
        logger.info("Habit added successfully: {} for user: {} with {} days",
                saved.getTitle(), userId, saved.getHabitDayWeeks().size());
        initializationHabitCompletion.addNewHabitToCompletion(saved);
        return Optional.of(HabitMapper.toResponse(saved));

    }

    @Transactional
    public Optional<HabitResponse> updateHabit(UUID userId, UUID habitId, HabitRequest request) {
        logger.debug("updateHabit called for userId: {}, habitId: {}", userId, habitId);

        if (request == null) {
            logger.warn("updateHabit failed: request is null");
            return Optional.empty();
        }

        User user = validateUser(userId);
        Habit existing = validateHabit(user, habitId);

        updateHabitFields(existing, request, userId);

        Habit saved = habitRepository.save(existing);
        logger.info("Habit updated successfully: {} for user: {} with {} days",
                saved.getTitle(), userId, saved.getHabitDayWeeks().size());

        initializationHabitCompletion.addNewHabitToCompletion(saved);
        return Optional.of(HabitMapper.toResponse(saved));
    }

    private User validateUser(UUID userId) {
        return getExistingUser(userId).orElseThrow(() -> {
            logger.warn("updateHabit failed: user not found with id: {}", userId);
            return new NoSuchElementException("User not found");
        });
    }

    private Habit validateHabit(User user, UUID habitId) {
        return habitRepository.findByHabitIdAndUserAndIsActiveTrue(habitId, user).orElseThrow(() -> {
            logger.warn("updateHabit failed: habit not found with id: {} for user: {}", habitId, user.getUserId());
            return new NoSuchElementException("Habit not found");
        });
    }

    private void updateHabitFields(Habit existing, HabitRequest request, UUID userId) {
        if (request.getTitle() != null && !request.getTitle().equals(existing.getTitle())) {
            if (existsByTitle(userId, request.getTitle())) {
                logger.warn("updateHabit failed: habit with title '{}' already exists for user: {}", request.getTitle(), userId);
                throw new IllegalArgumentException("Habit with this title already exists");
            }
            existing.setTitle(request.getTitle());
        }

        if (request.getPoint() > 0) {
            existing.setPoint(request.getPoint());
        }
        existing.setType(request.isType());
        if (request.getDescription() != null) {
            existing.setDescription(request.getDescription());
        }
        existing.setActive(request.isActive());

        updateCategory(existing, request);
        updateDayWeeks(existing, request);
    }

    private void updateCategory(Habit existing, HabitRequest request) {
        if (request.getHabitCategoryId() != null &&
                !request.getHabitCategoryId().equals(existing.getHabitCategory().getHabitCategoryId())) {
            HabitCategory category = getExistingCategory(request.getHabitCategoryId()).orElseThrow(() -> {
                logger.warn("updateHabit failed: category not found with id: {}", request.getHabitCategoryId());
                return new NoSuchElementException("Category not found");
            });
            existing.setHabitCategory(category);
        }
    }

    private void updateDayWeeks(Habit existing, HabitRequest request) {
        if (request.getHabitDayWeekIds() == null) return;

        existing.getHabitDayWeeks().clear();
        if (!request.getHabitDayWeekIds().isEmpty()) {
            List<HabitDayWeek> dayWeeks = habitDayWeekRepository.findAllById(request.getHabitDayWeekIds());
            if (dayWeeks.size() != request.getHabitDayWeekIds().size()) {
                logger.warn("updateHabit failed: some day week IDs were not found");
                throw new IllegalArgumentException("Some day of week IDs were not found");
            }
            existing.getHabitDayWeeks().addAll(dayWeeks);
        }
    }


    @Transactional
    public boolean deleteHabit(UUID userId, UUID habitId) {
        logger.debug("deleteHabit called for userId: {}, habitId: {}", userId, habitId);

        Optional<User> userOpt = getExistingUser(userId);
        if (userOpt.isEmpty()) {
            logger.warn("deleteHabit failed: user not found with id: {}", userId);
            return false;
        }

        if (habitId == null) {
            logger.warn("deleteHabit failed: habitId is null");
            return false;
        }

        Optional<Habit> habitOpt = habitRepository.findByHabitIdAndUserAndIsActiveTrue(habitId, userOpt.get());
        if (habitOpt.isEmpty()) {
            logger.warn("deleteHabit failed: habit not found with id: {} for user: {}", habitId, userId);
            return false;
        }

        Habit habit = habitOpt.get().deactivate();
        habitRepository.save(habit);
        logger.info("Habit deactivated successfully: {} for user: {}", habit.getTitle(), userId);
        return true;
    }

    public Optional<Habit> findEntityByIdAndUser(UUID userId, UUID habitId) {
        logger.debug("findEntityByIdAndUser called with userId={} habitId={}", userId, habitId);

        if (userId == null || habitId == null) {
            logger.warn("findEntityByIdAndUser failed: userId or habitId is null");
            return Optional.empty();
        }

        return getExistingUser(userId)
                .flatMap(user -> habitRepository.findByHabitIdAndUserAndIsActiveTrue(habitId, user));
    }
    public List<Habit> findEntityByHabitDayWeeks(HabitDayWeek habitDayWeek) {
        logger.debug("findEntityByHabitDayWeeks called with habitDayWeek={} habitDayWeek, habit",habitDayWeek);

        if (habitDayWeek == null) {
            logger.warn("findEntityByHabitDayWeeks failed: habitDayWeek is null");
            return Collections.emptyList();
        }

        return  habitRepository.findByHabitDayWeeks(habitDayWeek);
    }



    @Transactional
    public boolean permanentDeleteHabit(UUID userId, UUID habitId) {
        logger.debug("permanentDeleteHabit called for userId: {}, habitId: {}", userId, habitId);

        Optional<User> userOpt = getExistingUser(userId);
        if (userOpt.isEmpty()) {
            logger.warn("permanentDeleteHabit failed: user not found with id: {}", userId);
            return false;
        }

        if (habitId == null) {
            logger.warn("permanentDeleteHabit failed: habitId is null");
            return false;
        }

        Optional<Habit> habitOpt = habitRepository.findByHabitIdAndUser(habitId, userOpt.get());
        if (habitOpt.isEmpty()) {
            logger.warn("permanentDeleteHabit failed: habit not found with id: {} for user: {}", habitId, userId);
            return false;
        }

        habitRepository.delete(habitOpt.get());
        logger.info("Habit permanently deleted for user: {}", userId);
        return true;
    }

    public Optional<HabitResponse> findById(UUID userId, UUID habitId) {
        logger.debug("findById called for userId: {}, habitId: {}", userId, habitId);

        if (userId == null || habitId == null) {
            logger.warn("findById failed: userId or habitId is null");
            return Optional.empty();
        }

        Optional<User> userOpt = getExistingUser(userId);
        if (userOpt.isEmpty()) {
            logger.warn("findById failed: user not found with id: {}", userId);
            return Optional.empty();
        }

        return habitRepository.findByHabitIdAndUserAndIsActiveTrue(habitId, userOpt.get())
                .map(habit -> {
                    logger.info("Found habit: {} for user: {}", habit.getTitle(), userId);
                    return HabitMapper.toResponse(habit);
                });
    }


    public List<HabitResponse> searchByTitle(UUID userId, String title) {
        logger.debug("searchByTitle called for userId: {} with title: {}", userId, title);

        Optional<User> userOpt = getExistingUser(userId);
        if (userOpt.isEmpty() || title == null || title.isBlank()) {
            logger.warn("searchByTitle failed: user not found or title is blank");
            return Collections.emptyList();
        }

        List<Habit> habits = habitRepository.findByTitleContainingIgnoreCaseAndUserAndIsActiveTrue(title, userOpt.get());
        logger.info("Found {} habits with title containing: {} for user: {}", habits.size(), title, userId);

        return habits.stream()
                .map(HabitMapper::toResponse)
                .toList();
    }

    public Page<HabitResponse> filterHabits(UUID userId, UUID categoryId, Long dayWeekId,
                                            Boolean type, int pageNumber, int pageSize) {
        logger.debug("filterHabits called for userId: {} with filters - category: {}, dayWeek: {}, type: {}",
                userId, categoryId, dayWeekId, type);

        Optional<User> userOpt = getExistingUser(userId);
        if (userOpt.isEmpty()) {
            logger.warn("filterHabits failed: user not found with id: {}", userId);
            return Page.empty();
        }

        Optional<HabitCategory> categoryOpt = categoryId != null ? getExistingCategory(categoryId) : Optional.empty();
        Optional<HabitDayWeek> dayWeekOpt = dayWeekId != null ? getExistingHabitDayWeek(dayWeekId) : Optional.empty();

        if ((categoryId != null && categoryOpt.isEmpty()) ||
                (dayWeekId != null && dayWeekOpt.isEmpty())) {
            logger.warn("filterHabits failed: requested category or dayWeek not found");
            return Page.empty();
        }

        Pageable pageable = createPageable(pageNumber, pageSize, TITLE);
        Page<Habit> habits = habitRepository.filterHabits(
                userOpt.get(),
                categoryOpt.orElse(null),
                dayWeekOpt.orElse(null),
                type,
                pageable
        );

        logger.info("Found {} filtered habits for user: {}", habits.getTotalElements(), userId);
        return habits.map(HabitMapper::toResponse);
    }

    public Page<HabitResponse> pageableByType(UUID userId, boolean type, int pageNumber, int pageSize) {
        logger.debug("pageableByType called for userId: {} with type: {}", userId, type);

        Optional<User> userOpt = getExistingUser(userId);
        if (userOpt.isEmpty()) {
            logger.warn("pageableByType failed: user not found with id: {}", userId);
            return Page.empty();
        }

        Pageable pageable = createPageable(pageNumber, pageSize, TITLE);
        Page<Habit> habits = habitRepository.findByUserAndTypeAndIsActiveTrue(userOpt.get(), type, pageable);

        logger.info("Found {} habits with type: {} for user: {}", habits.getTotalElements(), type, userId);
        return habits.map(HabitMapper::toResponse);
    }

    public Page<HabitResponse> pageableByPoint(UUID userId, double point, int pageNumber, int pageSize) {
        logger.debug("pageableByPoint called for userId: {} with point: {}", userId, point);

        Optional<User> userOpt = getExistingUser(userId);
        if (userOpt.isEmpty()) {
            logger.warn("pageableByPoint failed: user not found with id: {}", userId);
            return Page.empty();
        }

        Pageable pageable = createPageable(pageNumber, pageSize, "point");
        Page<Habit> habits = habitRepository.findByUserAndPointAndIsActiveTrue(userOpt.get(), point, pageable);

        logger.info("Found {} habits with point: {} for user: {}", habits.getTotalElements(), point, userId);
        return habits.map(HabitMapper::toResponse);
    }

    public Page<HabitResponse> pageableAllActive(UUID userId, int pageNumber, int pageSize) {
        logger.debug("pageableAllActive called for userId: {}", userId);

        Optional<User> userOpt = getExistingUser(userId);
        if (userOpt.isEmpty()) {
            logger.warn("pageableAllActive failed: user not found with id: {}", userId);
            return Page.empty();
        }

        Pageable pageable = createPageable(pageNumber, pageSize, TITLE);
        Page<Habit> habits = habitRepository.findAllByUserAndIsActiveTrue(userOpt.get(), pageable);

        logger.info("Found {} active habits for user: {}", habits.getTotalElements(), userId);
        return habits.map(HabitMapper::toResponse);
    }

    public Page<HabitResponse> pageableAllInactive(UUID userId, int pageNumber, int pageSize) {
        logger.debug("pageableAllInactive called for userId: {}", userId);

        Optional<User> userOpt = getExistingUser(userId);
        if (userOpt.isEmpty()) {
            logger.warn("pageableAllInactive failed: user not found with id: {}", userId);
            return Page.empty();
        }

        Pageable pageable = createPageable(pageNumber, pageSize, TITLE);
        Page<Habit> habits = habitRepository.findAllByUserAndIsActiveFalse(userOpt.get(), pageable);

        logger.info("Found {} inactive habits for user: {}", habits.getTotalElements(), userId);
        return habits.map(HabitMapper::toResponse);
    }

    public Page<HabitResponse> pageableByCategory(UUID userId, UUID categoryId, int pageNumber, int pageSize) {
        logger.debug("pageableByCategory called for userId: {}, categoryId: {}", userId, categoryId);

        Optional<User> userOpt = getExistingUser(userId);
        Optional<HabitCategory> categoryOpt = getExistingCategory(categoryId);

        if (userOpt.isEmpty() || categoryOpt.isEmpty()) {
            logger.warn("pageableByCategory failed: user or category not found");
            return Page.empty();
        }

        Pageable pageable = createPageable(pageNumber, pageSize, TITLE);
        Page<Habit> habits = habitRepository.findByUserAndHabitCategoryAndIsActiveTrue(
                userOpt.get(), categoryOpt.get(), pageable);

        logger.info("Found {} habits in category for user: {}", habits.getTotalElements(), userId);
        return habits.map(HabitMapper::toResponse);
    }

    public Page<HabitResponse> pageableByDayOfWeek(UUID userId, long habitDayWeekId, int pageNumber, int pageSize) {
        logger.debug("pageableByDayOfWeek called for userId: {}, habitDayWeekId: {}", userId, habitDayWeekId);

        Optional<User> userOpt = getExistingUser(userId);
        Optional<HabitDayWeek> dayWeekOpt = getExistingHabitDayWeek(habitDayWeekId);

        if (userOpt.isEmpty() || dayWeekOpt.isEmpty()) {
            logger.warn("pageableByDayOfWeek failed: user or dayWeek not found");
            return Page.empty();
        }

        Pageable pageable = createPageable(pageNumber, pageSize, TITLE);
        Page<Habit> habits = habitRepository.findByUserAndHabitDayWeeksContainingAndIsActiveTrue(
                userOpt.get(), dayWeekOpt.get(), pageable);

        logger.info("Found {} habits for day of week for user: {}", habits.getTotalElements(), userId);
        return habits.map(HabitMapper::toResponse);
    }

    // ===== Helper Methods Exists =====

    public boolean existsById(UUID userId, UUID habitId) {
        logger.debug("existsById called for userId: {}, habitId: {}", userId, habitId);

        Optional<User> userOpt = getExistingUser(userId);
        if (userOpt.isEmpty() || habitId == null) {
            logger.warn("existsById failed: user not found or habitId is null");
            return false;
        }

        boolean exists = habitRepository.existsByHabitIdAndUserAndIsActiveTrue(habitId, userOpt.get());
        logger.info("Habit {} exists for user {}: {}", habitId, userId, exists);
        return exists;
    }

    public boolean existsByTitle(UUID userId, String title) {
        logger.debug("existsByTitle called for userId: {} with title: {}", userId, title);

        Optional<User> userOpt = getExistingUser(userId);
        if (userOpt.isEmpty() || title == null || title.isBlank()) {
            logger.warn("existsByTitle failed: user not found or title is blank");
            return false;
        }

        boolean exists = habitRepository.existsByTitleAndUserAndIsActiveTrue(title, userOpt.get());
        logger.info("Habit with title '{}' exists for user {}: {}", title, userId, exists);
        return exists;
    }

    // ===== Helper Methods Count =====

    public long countByUser(UUID userId) {
        logger.debug("countByUser called for userId: {}", userId);

        Optional<User> userOpt = getExistingUser(userId);
        if (userOpt.isEmpty()) {
            logger.warn("countByUser failed: user not found with id: {}", userId);
            return 0;
        }

        long count = habitRepository.countByUserAndIsActiveTrue(userOpt.get());
        logger.info("User {} has {} active habits", userId, count);
        return count;
    }

    public long countByPointLessThan(UUID userId, double point) {
        logger.debug("countByPointLessThan called for userId: {} with point: {}", userId, point);

        Optional<User> userOpt = getExistingUser(userId);
        if (userOpt.isEmpty()) {
            logger.warn("countByPointLessThan failed: user not found with id: {}", userId);
            return 0;
        }

        long count = habitRepository.countByUserAndPointLessThanAndIsActiveTrue(userOpt.get(), point);
        logger.info("User {} has {} habits with points less than {}", userId, count, point);
        return count;
    }

    public long countByPointGreaterThan(UUID userId, double point) {
        logger.debug("countByPointGreaterThan called for userId: {} with point: {}", userId, point);

        Optional<User> userOpt = getExistingUser(userId);
        if (userOpt.isEmpty()) {
            logger.warn("countByPointGreaterThan failed: user not found with id: {}", userId);
            return 0;
        }

        long count = habitRepository.countByUserAndPointGreaterThanAndIsActiveTrue(userOpt.get(), point);
        logger.info("User {} has {} habits with points greater than {}", userId, count, point);
        return count;
    }

    public long countByType(UUID userId, boolean type) {
        logger.debug("countByType called for userId: {} with type: {}", userId, type);

        Optional<User> userOpt = getExistingUser(userId);
        if (userOpt.isEmpty()) {
            logger.warn("countByType failed: user not found with id: {}", userId);
            return 0;
        }

        long count = habitRepository.countByUserAndTypeAndIsActiveTrue(userOpt.get(), type);
        logger.info("User {} has {} habits with type: {}", userId, count, type);
        return count;
    }

    public long countByCategory(UUID userId, UUID categoryId) {
        logger.debug("countByCategory called for userId: {}, categoryId: {}", userId, categoryId);

        Optional<User> userOpt = getExistingUser(userId);
        Optional<HabitCategory> categoryOpt = getExistingCategory(categoryId);
        if (userOpt.isEmpty() || categoryOpt.isEmpty()) {
            logger.warn("countByCategory failed: user or category not found");
            return 0;
        }

        long count = habitRepository.countByUserAndHabitCategoryAndIsActiveTrue(userOpt.get(), categoryOpt.get());
        logger.info("User {} has {} habits in category: {}", userId, count, categoryId);
        return count;
    }

    public long countByHabitDayWeek(UUID userId, long habitDayWeekId) {
        logger.debug("countByHabitDayWeek called for userId: {}, habitDayWeekId: {}", userId, habitDayWeekId);

        Optional<User> userOpt = getExistingUser(userId);
        Optional<HabitDayWeek> dayWeekOpt = getExistingHabitDayWeek(habitDayWeekId);
        if (userOpt.isEmpty() || dayWeekOpt.isEmpty()) {
            logger.warn("countByHabitDayWeek failed: user or dayWeek not found");
            return 0;
        }

        long count = habitRepository.countByUserAndHabitDayWeeksContainingAndIsActiveTrue(userOpt.get(), dayWeekOpt.get());
        logger.info("User {} has {} habits for day of week: {}", userId, count, habitDayWeekId);
        return count;
    }
}