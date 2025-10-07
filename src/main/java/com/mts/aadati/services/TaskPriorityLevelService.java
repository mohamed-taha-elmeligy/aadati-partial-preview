package com.mts.aadati.services;

import com.mts.aadati.entities.TaskPriorityLevel;
import com.mts.aadati.dto.mapper.TaskPriorityLevelMapper;
import com.mts.aadati.dto.request.TaskPriorityLevelRequest;
import com.mts.aadati.dto.response.TaskPriorityLevelResponse;
import com.mts.aadati.repository.TaskPriorityLevelRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */

@Service
@AllArgsConstructor
public class TaskPriorityLevelService {

    private final TaskPriorityLevelRepository taskPriorityLevelRepository;

    private static final Logger logger = LoggerFactory.getLogger(TaskPriorityLevelService.class);

    // ===== Helper Methods CRUD =====
    @Transactional
    public Optional<TaskPriorityLevelResponse> addTaskPriorityLevel(TaskPriorityLevelRequest request) {
        logger.debug("addTaskPriorityLevel called with priorityLevel: {}",
                request != null ? request.getPriorityLevel() : "null");

        if (request == null) {
            logger.warn("addTaskPriorityLevel failed: request is null");
            return Optional.empty();
        }

        if ( taskPriorityLevelRepository.existsByPriorityLevel(request.getPriorityLevel())) {
            logger.warn("addTaskPriorityLevel failed: PriorityLevel {} already exists", request.getPriorityLevel());
            throw new IllegalArgumentException("Priority level already exists");
        }

        TaskPriorityLevel entity = TaskPriorityLevelMapper.toEntity(request);
        TaskPriorityLevel saved = taskPriorityLevelRepository.save(entity);

        logger.info("TaskPriorityLevel added successfully with priorityLevel: {}", saved.getPriorityLevel());
        return Optional.of(TaskPriorityLevelMapper.toResponse(saved));
    }

    @Transactional
    public boolean addAllTaskPriorityLevel(List<TaskPriorityLevelRequest> requests) {
        logger.debug("addAllTaskPriorityLevel called with {} items",
                requests != null ? requests.size() : 0);

        if (requests == null || requests.isEmpty()) {
            logger.warn("addAllTaskPriorityLevel failed: requests list is null or empty");
            return false;
        }

        List<TaskPriorityLevel> toSave = new ArrayList<>();

        for (TaskPriorityLevelRequest request : requests) {
            if (request == null ) continue;

            Optional<TaskPriorityLevel> existing =
                    taskPriorityLevelRepository.findByPriorityLevel(request.getPriorityLevel());

            if (existing.isPresent()) {
                TaskPriorityLevel existingEntity = existing.get();
                existingEntity.setColor(request.getColor());
                existingEntity.setName(request.getName());
                toSave.add(existingEntity);
            } else {
                toSave.add(TaskPriorityLevelMapper.toEntity(request));
            }
        }

        taskPriorityLevelRepository.saveAll(toSave);
        logger.info("Successfully saved {} TaskPriorityLevel items", toSave.size());
        return true;
    }

    @Transactional
    public boolean addAllTaskPriorityLevelEntity(List<TaskPriorityLevel> entities) {
        logger.debug("addAllTaskPriorityLevelEntity called with {} items",
                entities != null ? entities.size() : 0);

        if (entities == null || entities.isEmpty()) {
            logger.warn("addAllTaskPriorityLevelEntity failed: entities list is null or empty");
            return false;
        }

        List<TaskPriorityLevel> toSave = new ArrayList<>();

        for (TaskPriorityLevel entity : entities) {
            if (entity == null ) continue;

            Optional<TaskPriorityLevel> existing =
                    taskPriorityLevelRepository.findByPriorityLevel(entity.getPriorityLevel());

            if (existing.isPresent()) {
                TaskPriorityLevel existingEntity = existing.get();
                existingEntity.setColor(entity.getColor());
                existingEntity.setName(entity.getName());
                toSave.add(existingEntity);
            } else {
                toSave.add(entity);
            }
        }

        taskPriorityLevelRepository.saveAll(toSave);
        logger.info("Successfully saved {} TaskPriorityLevel entities", toSave.size());
        return true;
    }

    @Transactional
    public Optional<TaskPriorityLevelResponse> updateTaskPriorityLevel(UUID id, TaskPriorityLevelRequest request) {
        logger.debug("updateTaskPriorityLevel called with id: {}", id);

        if (id == null || request == null) {
            logger.warn("updateTaskPriorityLevel failed: id or request is null");
            return Optional.empty();
        }

        Optional<TaskPriorityLevel> existing = taskPriorityLevelRepository.findById(id);
        if (existing.isEmpty()) {
            logger.warn("updateTaskPriorityLevel failed: TaskPriorityLevel not found with id: {}", id);
            throw new NoSuchElementException("TaskPriorityLevel not found");
        }

        TaskPriorityLevel entity = existing.get();

        if (request.getName() != null) entity.setName(request.getName());
        if (request.getColor() != null) entity.setColor(request.getColor());
        if (request.getPriorityLevel()!=entity.getPriorityLevel()) {
            if (taskPriorityLevelRepository.existsByPriorityLevel(request.getPriorityLevel())) {
                logger.warn("updateTaskPriorityLevel failed: PriorityLevel {} already exists", request.getPriorityLevel());
                throw new IllegalArgumentException("Priority level already exists");
            }
            entity.setPriorityLevel(request.getPriorityLevel());
        }

        TaskPriorityLevel saved = taskPriorityLevelRepository.save(entity);
        logger.info("TaskPriorityLevel updated successfully with id: {}", saved.getTaskPriorityLevelId());
        return Optional.of(TaskPriorityLevelMapper.toResponse(saved));
    }

    public boolean removePriorityLevelById(UUID id) {
        logger.debug("removePriorityLevelById called with id: {}", id);

        if (id == null) {
            logger.warn("removePriorityLevelById failed: id is null");
            return false;
        }

        if (!taskPriorityLevelRepository.existsById(id)) {
            logger.warn("removePriorityLevelById failed: TaskPriorityLevel not found with id: {}", id);
            return false;
        }

        taskPriorityLevelRepository.deleteById(id);
        logger.info("TaskPriorityLevel deleted successfully with id: {}", id);
        return true;
    }

    @Transactional
    public boolean removeAllPriorityLevelById(List<UUID> ids) {
        logger.debug("removeAllPriorityLevelById called with {} ids", ids != null ? ids.size() : 0);

        if (ids == null || ids.isEmpty()) {
            logger.warn("removeAllPriorityLevelById failed: ids list is null or empty");
            return false;
        }

        List<TaskPriorityLevel> toDelete = taskPriorityLevelRepository.findAllById(ids);
        if (toDelete.isEmpty()) {
            logger.warn("removeAllPriorityLevelById failed: No TaskPriorityLevel found for given ids");
            return false;
        }

        taskPriorityLevelRepository.deleteAll(toDelete);
        logger.info("Successfully deleted {} TaskPriorityLevel items", toDelete.size());
        return true;
    }

    public Optional<TaskPriorityLevelResponse> searchById(UUID id) {
        logger.debug("searchById called with id: {}", id);

        if (id == null) {
            logger.warn("searchById failed: id is null");
            return Optional.empty();
        }

        return taskPriorityLevelRepository.findById(id)
                .map(entity -> {
                    logger.info("Found TaskPriorityLevel with id: {}", id);
                    return TaskPriorityLevelMapper.toResponse(entity);
                });
    }

    public Optional<TaskPriorityLevelResponse> searchByPriorityLevel(Integer priorityLevel) {
        logger.debug("searchByPriorityLevel called with priorityLevel: {}", priorityLevel);

        if (priorityLevel == null || priorityLevel <= 0) {
            logger.warn("searchByPriorityLevel failed: priorityLevel is null or invalid");
            return Optional.empty();
        }

        return taskPriorityLevelRepository.searchByPriorityLevel(priorityLevel)
                .map(entity -> {
                    logger.info("Found TaskPriorityLevel with priorityLevel: {}", priorityLevel);
                    return TaskPriorityLevelMapper.toResponse(entity);
                });
    }

    public List<TaskPriorityLevelResponse> searchByPriorityLevelOrColor(Integer priorityLevel, String color) {
        logger.debug("searchByPriorityLevelOrColor called with priorityLevel: {}, color: {}", priorityLevel, color);

        boolean validPriority = (priorityLevel != null && priorityLevel > 0);
        boolean validColor = (color != null && !color.trim().isEmpty());

        if (!validPriority && !validColor) {
            logger.warn("searchByPriorityLevelOrColor failed: both parameters are invalid");
            return Collections.emptyList();
        }

        List<TaskPriorityLevel> results = taskPriorityLevelRepository.searchByPriorityLevelOrColor(priorityLevel, color);
        logger.info("Found {} searchByPriorityLevelOrColor items", results.size());

        return results.stream()
                .map(TaskPriorityLevelMapper::toResponse)
                .toList();
    }

    // ===== Helper Methods Show =====
    public List<String> showAllColor() {
        logger.debug("showAllColor called");

        List<String> colors = Optional.ofNullable(taskPriorityLevelRepository.getAllColor())
                .orElse(Collections.emptyList());

        logger.info("Found {} colors", colors.size());
        return colors;
    }

    public List<TaskPriorityLevelResponse> showByPriorityLevelAsc() {
        logger.debug("showByPriorityLevelAsc called");

        List<TaskPriorityLevel> results = Optional.ofNullable(
                        taskPriorityLevelRepository.findAllByOrderByPriorityLevelAsc())
                .orElse(Collections.emptyList());

        logger.info("Found {} TaskPriorityLevel items ordered by priority asc", results.size());
        return results.stream()
                .map(TaskPriorityLevelMapper::toResponse)
                .toList();
    }

    public List<TaskPriorityLevelResponse> showByPriorityLevelDesc() {
        logger.debug("showByPriorityLevelDesc called");

        List<TaskPriorityLevel> results = Optional.ofNullable(
                        taskPriorityLevelRepository.findAllByOrderByPriorityLevelDesc())
                .orElse(Collections.emptyList());

        logger.info("Found {} TaskPriorityLevel items ordered by priority desc", results.size());
        return results.stream()
                .map(TaskPriorityLevelMapper::toResponse)
                .toList();
    }

    public List<TaskPriorityLevelResponse> showAllTaskPriorityLevel() {
        logger.debug("showAllTaskPriorityLevel called");

        List<TaskPriorityLevel> results = taskPriorityLevelRepository.findAll();
        logger.info("Found {} TaskPriorityLevel items", results.size());

        return results.stream()
                .map(TaskPriorityLevelMapper::toResponse)
                .toList();
    }

    // ===== Helper Methods Find =====
    public boolean existsPriorityLevel(Integer priorityLevel) {
        logger.debug("existsPriorityLevel called with priorityLevel: {}", priorityLevel);

        if (priorityLevel == null || priorityLevel <= 0) {
            logger.warn("existsPriorityLevel failed: priorityLevel is null or invalid");
            return false;
        }

        boolean exists = taskPriorityLevelRepository.existsByPriorityLevel(priorityLevel);
        logger.info("PriorityLevel {} exists: {}", priorityLevel, exists);
        return exists;
    }

}