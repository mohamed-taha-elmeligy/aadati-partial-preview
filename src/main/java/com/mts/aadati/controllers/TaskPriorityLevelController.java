package com.mts.aadati.controllers;

import com.mts.aadati.dto.request.TaskPriorityLevelRequest;
import com.mts.aadati.dto.response.TaskPriorityLevelResponse;
import com.mts.aadati.services.TaskPriorityLevelService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */

@RestController
@RequestMapping("/aadati/v1/task-priority-level")
@AllArgsConstructor
public class TaskPriorityLevelController {

    private final TaskPriorityLevelService taskPriorityLevelService;

    private static final Logger logger = LoggerFactory.getLogger(TaskPriorityLevelController.class);

    // ===== Helper Methods CRUD =====

    @PostMapping("/add")
    public ResponseEntity<TaskPriorityLevelResponse> addTaskPriorityLevel(
            @RequestBody @Valid TaskPriorityLevelRequest request) {
        logger.debug("POST /add called with priorityLevel: {}",
                request != null ? request.getPriorityLevel() : "null");

        try {
            Optional<TaskPriorityLevelResponse> result = taskPriorityLevelService.addTaskPriorityLevel(request);

            if (result.isPresent()) {
                logger.info("TaskPriorityLevel created successfully with id: {}", result.get().getTaskPriorityLevelId());
                return ResponseEntity.status(HttpStatus.CREATED).body(result.get());
            } else {
                logger.warn("Failed to create TaskPriorityLevel");
                return ResponseEntity.badRequest().build();
            }
        } catch (IllegalArgumentException e) {
            logger.warn("Bad request for add TaskPriorityLevel: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Internal error while adding TaskPriorityLevel: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/add-all")
    public ResponseEntity<String> addAllTaskPriorityLevel(
            @RequestBody @Valid List<TaskPriorityLevelRequest> requests) {
        logger.debug("POST /add-all called with {} items", requests != null ? requests.size() : 0);

        try {
            boolean result = taskPriorityLevelService.addAllTaskPriorityLevel(requests);

            if (result) {
                logger.info("Successfully processed TaskPriorityLevel items");
                return ResponseEntity.ok("TaskPriorityLevels processed successfully");
            } else {
                logger.warn("Failed to process TaskPriorityLevel items");
                return ResponseEntity.badRequest().body("Failed to process TaskPriorityLevel items");
            }
        } catch (Exception e) {
            logger.error("Internal error while processing TaskPriorityLevel items: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Internal server error");
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<TaskPriorityLevelResponse> updateTaskPriorityLevel(
            @PathVariable UUID id,
            @RequestBody @Valid TaskPriorityLevelRequest request) {
        logger.debug("PUT /update/{} called", id);

        try {
            Optional<TaskPriorityLevelResponse> result = taskPriorityLevelService.updateTaskPriorityLevel(id, request);

            if (result.isPresent()) {
                logger.info("TaskPriorityLevel updated successfully with id: {}", id);
                return ResponseEntity.ok(result.get());
            } else {
                logger.warn("TaskPriorityLevel not found for update with id: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            logger.warn("Bad request for update TaskPriorityLevel: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Internal error while updating TaskPriorityLevel: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<Void> removePriorityLevelById(@PathVariable UUID id) {
        logger.debug("DELETE /remove/{} called", id);

        try {
            boolean result = taskPriorityLevelService.removePriorityLevelById(id);

            if (result) {
                logger.info("TaskPriorityLevel deleted successfully with id: {}", id);
                return ResponseEntity.noContent().build();
            } else {
                logger.warn("TaskPriorityLevel not found for deletion with id: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Internal error while deleting TaskPriorityLevel: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/remove-all")
    public ResponseEntity<String> removeAllPriorityLevelById(
            @RequestBody @Valid List<UUID> ids) {
        logger.debug("DELETE /remove-all called with {} ids", ids != null ? ids.size() : 0);

        try {
            boolean result = taskPriorityLevelService.removeAllPriorityLevelById(ids);

            if (result) {
                logger.info("Successfully deleted TaskPriorityLevel items");
                return ResponseEntity.ok("TaskPriorityLevels deleted successfully");
            } else {
                logger.warn("No TaskPriorityLevel items found for deletion");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Internal error while deleting TaskPriorityLevel items: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // ===== Helper Methods Show =====

    @GetMapping("/all")
    public ResponseEntity<List<TaskPriorityLevelResponse>> getAll() {
        logger.debug("GET /all called");

        try {
            List<TaskPriorityLevelResponse> results = taskPriorityLevelService.showAllTaskPriorityLevel();
            logger.info("Retrieved {} TaskPriorityLevel items", results.size());
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            logger.error("Internal error while retrieving all TaskPriorityLevel: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/colors")
    public ResponseEntity<List<String>> getColors() {
        logger.debug("GET /colors called");

        try {
            List<String> colors = taskPriorityLevelService.showAllColor();
            logger.info("Retrieved {} colors", colors.size());
            return ResponseEntity.ok(colors);
        } catch (Exception e) {
            logger.error("Internal error while retrieving colors: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/asc")
    public ResponseEntity<List<TaskPriorityLevelResponse>> getPriorityLevelAsc() {
        logger.debug("GET /asc called");

        try {
            List<TaskPriorityLevelResponse> results = taskPriorityLevelService.showByPriorityLevelAsc();
            logger.info("Retrieved TaskPriorityLevel items ordered by priority asc");
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            logger.error("Internal error while retrieving TaskPriorityLevel asc: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/desc")
    public ResponseEntity<List<TaskPriorityLevelResponse>> getPriorityLevelDesc() {
        logger.debug("GET /desc called");

        try {
            List<TaskPriorityLevelResponse> results = taskPriorityLevelService.showByPriorityLevelDesc();
            logger.info("Retrieved {} TaskPriorityLevel items ordered by priority desc", results.size());
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            logger.error("Internal error while retrieving TaskPriorityLevel desc: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // ===== Helper Methods Search =====

    @GetMapping("/search/id/{id}")
    public ResponseEntity<TaskPriorityLevelResponse> searchById(@PathVariable UUID id) {
        logger.debug("GET /search/id/{} called", id);

        try {
            Optional<TaskPriorityLevelResponse> result = taskPriorityLevelService.searchById(id);

            if (result.isPresent()) {
                logger.info("Found TaskPriorityLevel with id: {}", id);
                return ResponseEntity.ok(result.get());
            } else {
                logger.warn("TaskPriorityLevel not found with id: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Internal error while searching TaskPriorityLevel by id: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/search/level/{priorityLevel}")
    public ResponseEntity<TaskPriorityLevelResponse> searchByPriorityLevel(
            @PathVariable
            @Min(value = 1, message = "Priority level must be at least 1")
            @Max(value = 10, message = "Priority level cannot exceed 10")
            Integer priorityLevel) {
        logger.debug("GET /search/level/{} called", priorityLevel);

        try {
            Optional<TaskPriorityLevelResponse> result = taskPriorityLevelService.searchByPriorityLevel(priorityLevel);

            if (result.isPresent()) {
                logger.info("Found TaskPriorityLevel with priority level: {}", priorityLevel);
                return ResponseEntity.ok(result.get());
            } else {
                logger.warn("TaskPriorityLevel not found with priority level: {}", priorityLevel);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Internal error while searching TaskPriorityLevel by priority level: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<TaskPriorityLevelResponse>> searchByPriorityLevelOrColor(
            @RequestParam(required = false)
            @Min(value = 1, message = "Priority level must be at least 1")
            @Max(value = 10, message = "Priority level cannot exceed 10")
            Integer priorityLevel,

            @RequestParam(required = false)
            @Size(min = 2, max = 50, message = "Color must be between 2 and 50 characters")
            String color) {
        logger.debug("GET /search called with priorityLevel: {}, color: {}", priorityLevel, color);

        try {
            List<TaskPriorityLevelResponse> results = taskPriorityLevelService.searchByPriorityLevelOrColor(priorityLevel, color);
            logger.info("Found {} TaskPriorityLevel items", results.size());
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            logger.error("Internal error while searching TaskPriorityLevel: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/exists/{priorityLevel}")
    public ResponseEntity<Boolean> existsByPriorityLevel(
            @PathVariable
            @Min(value = 1, message = "Priority level must be at least 1")
            @Max(value = 10, message = "Priority level cannot exceed 10")
            Integer priorityLevel) {
        logger.debug("GET /exists/{} called", priorityLevel);

        try {
            boolean exists = taskPriorityLevelService.existsPriorityLevel(priorityLevel);
            logger.info("PriorityLevel {} exists: {}", priorityLevel, exists);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            logger.error("Internal error while checking if TaskPriorityLevel exists: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}