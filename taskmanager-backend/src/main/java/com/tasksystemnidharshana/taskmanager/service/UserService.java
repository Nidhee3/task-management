package com.tasksystemnidharshana.taskmanager.service;

import com.tasksystemnidharshana.taskmanager.payload.UserResponseDto;
import java.util.List;

//all users and particular user
public interface UserService {
    List<UserResponseDto> getAllUsers();
    UserResponseDto getUserById(Long id);
}