package com.tasksystemnidharshana.taskmanager.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagedTaskResponse {

    private List<TaskResponseDto> tasks;   
    private int currentPage;              
    private int totalPages;             
    private long totalTasks;             
    private boolean hasNext;             
    private boolean hasPrevious;         
}