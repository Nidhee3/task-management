package com.tasksystemnidharshana.taskmanager.payload;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {
    // title is required
    @NotBlank(message = "Title is required")
    private String title;
    //description is optional
    private String description;

    private String status;
    private String priority;
    //already there
    private Long assignedToId;
}