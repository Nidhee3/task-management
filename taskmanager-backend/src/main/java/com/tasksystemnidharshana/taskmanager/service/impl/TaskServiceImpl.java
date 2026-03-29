package com.tasksystemnidharshana.taskmanager.service.impl;

import com.tasksystemnidharshana.taskmanager.entity.Task;
import com.tasksystemnidharshana.taskmanager.entity.User;
import com.tasksystemnidharshana.taskmanager.exception.ResourceNotFoundException;
import com.tasksystemnidharshana.taskmanager.exception.TaskApiException;
import com.tasksystemnidharshana.taskmanager.payload.PagedTaskResponse;
import com.tasksystemnidharshana.taskmanager.payload.TaskDto;
import com.tasksystemnidharshana.taskmanager.payload.TaskResponseDto;
import com.tasksystemnidharshana.taskmanager.repository.TaskRepository;
import com.tasksystemnidharshana.taskmanager.repository.UserRepository;
import com.tasksystemnidharshana.taskmanager.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private static final List<String> VALID_STATUSES =
            Arrays.asList("TODO", "IN_PROGRESS", "DONE");

    private static final List<String> VALID_PRIORITIES =
            Arrays.asList("HIGH", "MEDIUM", "LOW");

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    // ─── CREATE ─────────────────────────────────────────────────────────────
    @Override
    public TaskResponseDto createTask(TaskDto taskDto, String currentUserEmail) {

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

        // Admin can assign to anyone; regular user always assigned to themselves
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
    public PagedTaskResponse getAllTasks(String status, Long assignedToId,
                                         String priority, String search,
                                         int page, int size,
                                         String currentUserEmail) {

        // Validate filters
        if (status != null && !VALID_STATUSES.contains(status.toUpperCase())) {
            throw new TaskApiException(HttpStatus.BAD_REQUEST,
                    "Invalid status. Must be one of: TODO, IN_PROGRESS, DONE");
        }
        if (priority != null && !VALID_PRIORITIES.contains(priority.toUpperCase())) {
            throw new TaskApiException(HttpStatus.BAD_REQUEST,
                    "Invalid priority. Must be one of: HIGH, MEDIUM, LOW");
        }

        String statusUpper   = (status   != null) ? status.toUpperCase()   : null;
        String priorityUpper = (priority != null) ? priority.toUpperCase() : null;
        
        String searchTerm    = (search   != null && !search.isBlank()) ? search.trim() : null;

        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new TaskApiException(HttpStatus.NOT_FOUND,
                        "Logged-in user not found"));

        Page<Task> taskPage;

        if (currentUser.getRole().equals("ADMIN")) {
            taskPage = taskRepository.findByFilters(
                    statusUpper, assignedToId, priorityUpper, searchTerm, pageable);
        } else {
            taskPage = taskRepository.findMyTasks(
                    currentUser.getId(), statusUpper, priorityUpper, searchTerm, pageable);
        }

        
        List<TaskResponseDto> taskDtos = taskPage.getContent()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return new PagedTaskResponse(
                taskDtos,
                taskPage.getNumber(),        
                taskPage.getTotalPages(),    
                taskPage.getTotalElements(), 
                taskPage.hasNext(),          
                taskPage.hasPrevious()       
        );
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

        boolean isCreator  = task.getCreatedBy().getId().equals(currentUser.getId());
        boolean isAssigned = task.getAssignedTo() != null &&
                task.getAssignedTo().getId().equals(currentUser.getId());
        boolean isAdmin    = currentUser.getRole().equals("ADMIN");

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