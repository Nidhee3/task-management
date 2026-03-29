package com.tasksystemnidharshana.taskmanager.payload;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {

    //title is mandatory
    @NotBlank(message = "Title is required")
    private String title;

    // description is optional
    private String description;

    // compulsory
    @NotBlank(message = "Status is required")
    private String status;

    // compulsory
    @NotBlank(message = "Priority is required")
    private String priority;

    private Long assignedToId;
}