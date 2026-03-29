package com.tasksystemnidharshana.taskmanager.controller;

import com.tasksystemnidharshana.taskmanager.payload.JwtAuthResponse;
import com.tasksystemnidharshana.taskmanager.payload.LoginDto;
import com.tasksystemnidharshana.taskmanager.payload.RegisterDto;
import com.tasksystemnidharshana.taskmanager.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    //registering a new user
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterDto registerDto) {
        String result = authService.register(registerDto);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }
    //login
    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@Valid @RequestBody LoginDto loginDto) {
        JwtAuthResponse response = authService.login(loginDto);
        return ResponseEntity.ok(response);
    }
}