package com.tasksystemnidharshana.taskmanager.controller;

import com.tasksystemnidharshana.taskmanager.payload.PagedTaskResponse;
import com.tasksystemnidharshana.taskmanager.payload.TaskDto;
import com.tasksystemnidharshana.taskmanager.payload.TaskResponseDto;
import com.tasksystemnidharshana.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    // Create a new task
    @PostMapping
    public ResponseEntity<TaskResponseDto> createTask(
            @Valid @RequestBody TaskDto taskDto,
            @AuthenticationPrincipal UserDetails currentUser) {
        TaskResponseDto response = taskService.createTask(taskDto, currentUser.getUsername());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Get all tasks 
    @GetMapping
    public ResponseEntity<PagedTaskResponse> getAllTasks(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long assignedTo,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0")  int page,   // which page (0 = first)
            @RequestParam(defaultValue = "6")  int size,   // tasks per page
            @AuthenticationPrincipal UserDetails currentUser) {

        PagedTaskResponse response = taskService.getAllTasks(
                status, assignedTo, priority, search, page, size, currentUser.getUsername());
        return ResponseEntity.ok(response);
    }

    // Get one task by ID
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable Long id) {
        TaskResponseDto task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    // Update a task
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskDto taskDto,
            @AuthenticationPrincipal UserDetails currentUser) {
        TaskResponseDto response = taskService.updateTask(id, taskDto, currentUser.getUsername());
        return ResponseEntity.ok(response);
    }

    // Delete a task (Admin only)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok("Task deleted successfully.");
    }
}