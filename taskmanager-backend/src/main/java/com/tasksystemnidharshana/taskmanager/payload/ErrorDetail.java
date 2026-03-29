package com.tasksystemnidharshana.taskmanager.payload;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

//tells when error happened, message of the error and details
public class ErrorDetail {
    private Date timeStamp;
    private String message;
    private String details;


}
