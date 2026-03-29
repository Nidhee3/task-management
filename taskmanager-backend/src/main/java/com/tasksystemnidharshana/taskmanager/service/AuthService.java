package com.tasksystemnidharshana.taskmanager.service;

import com.tasksystemnidharshana.taskmanager.payload.JwtAuthResponse;
import com.tasksystemnidharshana.taskmanager.payload.LoginDto;
import com.tasksystemnidharshana.taskmanager.payload.RegisterDto;

//auth has 2 things - login and register
public interface AuthService {
    String register(RegisterDto registerDto);
    JwtAuthResponse login(LoginDto loginDto);
}