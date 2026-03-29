package com.tasksystemnidharshana.taskmanager.service.impl;

import com.tasksystemnidharshana.taskmanager.entity.User;
import com.tasksystemnidharshana.taskmanager.exception.ResourceNotFoundException;
import com.tasksystemnidharshana.taskmanager.payload.UserResponseDto;
import com.tasksystemnidharshana.taskmanager.repository.UserRepository;
import com.tasksystemnidharshana.taskmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    //getting all users
    @Override
    public List<UserResponseDto> getAllUsers() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    //get user by id
    @Override
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return mapToDto(user);
    }
    //helps to give response
    private UserResponseDto mapToDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}