package com.tasksystemnidharshana.taskmanager.controller;

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

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;
    //creating a new task
    @PostMapping
    public ResponseEntity<TaskResponseDto> createTask(
            @Valid @RequestBody TaskDto taskDto,
            @AuthenticationPrincipal UserDetails currentUser) {
        TaskResponseDto response = taskService.createTask(taskDto, currentUser.getUsername());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    //getting all tasks
    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> getAllTasks(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long assignedTo,
            @RequestParam(required = false) String priority,
            @AuthenticationPrincipal UserDetails currentUser) {

        List<TaskResponseDto> tasks = taskService.getAllTasks(
                status, assignedTo, priority, currentUser.getUsername());
        return ResponseEntity.ok(tasks);
    }
    //getting a task
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable Long id) {
        TaskResponseDto task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }
    //updating task details
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskDto taskDto,
            @AuthenticationPrincipal UserDetails currentUser) {
        TaskResponseDto response = taskService.updateTask(
                id, taskDto, currentUser.getUsername());
        return ResponseEntity.ok(response);
    }
    //deleting a task
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok("Task deleted successfully.");
    }
}