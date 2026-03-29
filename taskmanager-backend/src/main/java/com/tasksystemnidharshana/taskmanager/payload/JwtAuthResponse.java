package com.tasksystemnidharshana.taskmanager.payload;

import lombok.Data;
@Data

public class JwtAuthResponse {

    private String accessToken;
    //set bearer as the default value
    private String tokenType = "Bearer";
    private String role;

    public JwtAuthResponse(String accessToken, String role) {
        this.accessToken = accessToken;
        this.role = role;
    }
}