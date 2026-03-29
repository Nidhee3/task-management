package com.tasksystemnidharshana.taskmanager.service;

import com.tasksystemnidharshana.taskmanager.payload.PagedTaskResponse;
import com.tasksystemnidharshana.taskmanager.payload.TaskDto;
import com.tasksystemnidharshana.taskmanager.payload.TaskResponseDto;

public interface TaskService {

    TaskResponseDto createTask(TaskDto taskDto, String currentUserEmail);

    
    PagedTaskResponse getAllTasks(String status, Long assignedToId,
                                  String priority, String search,
                                  int page, int size,
                                  String currentUserEmail);

    TaskResponseDto getTaskById(Long id);

    TaskResponseDto updateTask(Long id, TaskDto taskDto, String currentUserEmail);

    void deleteTask(Long id);
}