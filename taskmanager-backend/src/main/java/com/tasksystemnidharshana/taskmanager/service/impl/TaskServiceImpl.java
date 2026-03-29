package com.tasksystemnidharshana.taskmanager.service.impl;

import com.tasksystemnidharshana.taskmanager.entity.Task;
import com.tasksystemnidharshana.taskmanager.entity.User;
import com.tasksystemnidharshana.taskmanager.exception.ResourceNotFoundException;
import com.tasksystemnidharshana.taskmanager.exception.TaskApiException;
import com.tasksystemnidharshana.taskmanager.payload.TaskDto;
import com.tasksystemnidharshana.taskmanager.payload.TaskResponseDto;
import com.tasksystemnidharshana.taskmanager.repository.TaskRepository;
import com.tasksystemnidharshana.taskmanager.repository.UserRepository;
import com.tasksystemnidharshana.taskmanager.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {
//all task operations
    private static final List<String> VALID_STATUSES =
            Arrays.asList("TODO", "IN_PROGRESS", "DONE");

    private static final List<String> VALID_PRIORITIES =
            Arrays.asList("HIGH", "MEDIUM", "LOW");

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;
    //create task
    @Override
    public TaskResponseDto createTask(TaskDto taskDto, String currentUserEmail) {
        //check if user exists
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new TaskApiException(HttpStatus.NOT_FOUND,
                        "Logged-in user not found"));

        Task task = new Task();
        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setCreatedBy(currentUser);

        if (taskDto.getStatus() != null && !taskDto.getStatus().isBlank()) {
            validateStatus(taskDto.getStatus());
            task.setStatus(taskDto.getStatus().toUpperCase());
        }

        if (taskDto.getPriority() != null && !taskDto.getPriority().isBlank()) {
            validatePriority(taskDto.getPriority());
            task.setPriority(taskDto.getPriority().toUpperCase());
        }

        // Admin can assign to anyone , User always assigned to themselves
        if (currentUser.getRole().equals("ADMIN") && taskDto.getAssignedToId() != null) {
            User assignedUser = userRepository.findById(taskDto.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "User", "id", taskDto.getAssignedToId()));
            task.setAssignedTo(assignedUser);
        } else {
            task.setAssignedTo(currentUser);
        }

        Task savedTask = taskRepository.save(task);
        return mapToDto(savedTask);
    }

    @Override
    public List<TaskResponseDto> getAllTasks(String status, Long assignedToId,
                                             String priority, String currentUserEmail) {
        //check if status and priority are valid or not
        if (status != null && !VALID_STATUSES.contains(status.toUpperCase())) {
            throw new TaskApiException(HttpStatus.BAD_REQUEST,
                    "Invalid status. Must be one of: TODO, IN_PROGRESS, DONE");
        }
        if (priority != null && !VALID_PRIORITIES.contains(priority.toUpperCase())) {
            throw new TaskApiException(HttpStatus.BAD_REQUEST,
                    "Invalid priority. Must be one of: HIGH, MEDIUM, LOW");
        }

        String statusUpper = (status != null) ? status.toUpperCase() : null;
        String priorityUpper = (priority != null) ? priority.toUpperCase() : null;

        // Find the caller to check their role
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new TaskApiException(HttpStatus.NOT_FOUND,
                        "Logged-in user not found"));

        List<Task> tasks;

        if (currentUser.getRole().equals("ADMIN")) {
            // Admin can also filter by assignedTo (useful for monitoring)
            tasks = taskRepository.findByFilters(statusUpper, assignedToId, priorityUpper);
        } else {
            // User sees only tasks created by them or assigned to them
            tasks = taskRepository.findMyTasks(
                    currentUser.getId(), statusUpper, priorityUpper);
        }

        return tasks.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public TaskResponseDto getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));
        return mapToDto(task);
    }

    @Override
    public TaskResponseDto updateTask(Long id, TaskDto taskDto, String currentUserEmail) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));

        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new TaskApiException(HttpStatus.NOT_FOUND,
                        "Logged-in user not found"));

        boolean isCreator = task.getCreatedBy().getId().equals(currentUser.getId());
        boolean isAssigned = task.getAssignedTo() != null &&
                task.getAssignedTo().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole().equals("ADMIN");

        if (!isCreator && !isAssigned && !isAdmin) {
            throw new TaskApiException(HttpStatus.FORBIDDEN,
                    "You do not have permission to update this task.");
        }

        task.setTitle(taskDto.getTitle());
        if (taskDto.getDescription() != null) {
            task.setDescription(taskDto.getDescription());
        }

        if (taskDto.getStatus() != null && !taskDto.getStatus().isBlank()) {
            validateStatus(taskDto.getStatus());
            task.setStatus(taskDto.getStatus().toUpperCase());
        }

        if (taskDto.getPriority() != null && !taskDto.getPriority().isBlank()) {
            validatePriority(taskDto.getPriority());
            task.setPriority(taskDto.getPriority().toUpperCase());
        }

        // Only admin can reassign
        if (isAdmin && taskDto.getAssignedToId() != null) {
            User assignedUser = userRepository.findById(taskDto.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "User", "id", taskDto.getAssignedToId()));
            task.setAssignedTo(assignedUser);
        }

        Task updatedTask = taskRepository.save(task);
        return mapToDto(updatedTask);
    }

    @Override
    //deleting a task
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));
        taskRepository.delete(task);
    }



    private void validateStatus(String status) {
        if (!VALID_STATUSES.contains(status.toUpperCase())) {
            throw new TaskApiException(HttpStatus.BAD_REQUEST,
                    "Invalid status '" + status + "'. Allowed: TODO, IN_PROGRESS, DONE");
        }
    }

    private void validatePriority(String priority) {
        if (!VALID_PRIORITIES.contains(priority.toUpperCase())) {
            throw new TaskApiException(HttpStatus.BAD_REQUEST,
                    "Invalid priority '" + priority + "'. Allowed: HIGH, MEDIUM, LOW");
        }
    }

    private TaskResponseDto mapToDto(Task task) {
        TaskResponseDto dto = new TaskResponseDto();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setPriority(task.getPriority());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());

        if (task.getCreatedBy() != null) {
            dto.setCreatedById(task.getCreatedBy().getId());
            dto.setCreatedByName(task.getCreatedBy().getName());
        }

        if (task.getAssignedTo() != null) {
            dto.setAssignedToId(task.getAssignedTo().getId());
            dto.setAssignedToName(task.getAssignedTo().getName());
        }

        return dto;
    }
}