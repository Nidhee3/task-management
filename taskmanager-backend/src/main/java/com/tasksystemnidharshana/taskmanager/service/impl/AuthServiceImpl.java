package com.tasksystemnidharshana.taskmanager.service.impl;

import com.tasksystemnidharshana.taskmanager.entity.User;
import com.tasksystemnidharshana.taskmanager.exception.TaskApiException;
import com.tasksystemnidharshana.taskmanager.payload.JwtAuthResponse;
import com.tasksystemnidharshana.taskmanager.payload.LoginDto;
import com.tasksystemnidharshana.taskmanager.payload.RegisterDto;
import com.tasksystemnidharshana.taskmanager.repository.UserRepository;
import com.tasksystemnidharshana.taskmanager.security.JwtTokenProvider;
import com.tasksystemnidharshana.taskmanager.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public String register(RegisterDto registerDto) {
        //does this email exist
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new TaskApiException(HttpStatus.BAD_REQUEST,
                    "Email is already registered. Please use a different email.");
        }
        User user = new User();
        user.setName(registerDto.getName());
        user.setEmail(registerDto.getEmail());
        //new user registration
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setRole("USER");
        userRepository.save(user);

        return "User registered successfully!";
    }

    @Override
    public JwtAuthResponse login(LoginDto loginDto) {
        //login with email and password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(),
                        loginDto.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);
        //if user not there
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new TaskApiException(HttpStatus.NOT_FOUND, "User not found"));
        return new JwtAuthResponse(token, user.getRole());
    }
}