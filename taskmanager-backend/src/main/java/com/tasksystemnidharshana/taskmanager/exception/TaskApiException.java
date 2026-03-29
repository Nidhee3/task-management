package com.tasksystemnidharshana.taskmanager.exception;

import org.springframework.http.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class TaskApiException extends RuntimeException {
    private HttpStatus status;
    private String message;
}
