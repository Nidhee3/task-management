package com.tasksystemnidharshana.taskmanager.service;

import com.tasksystemnidharshana.taskmanager.payload.TaskDto;
import com.tasksystemnidharshana.taskmanager.payload.TaskResponseDto;

import java.util.List;

public interface TaskService {

    TaskResponseDto createTask(TaskDto taskDto, String currentUserEmail);

    // Admin all tasks , User - only their tasks.
    List<TaskResponseDto> getAllTasks(String status, Long assignedToId,
                                      String priority, String currentUserEmail);

    TaskResponseDto getTaskById(Long id);

    TaskResponseDto updateTask(Long id, TaskDto taskDto, String currentUserEmail);

    void deleteTask(Long id);
}