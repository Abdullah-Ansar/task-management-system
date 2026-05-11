package com.example.taskmanager.service;

import com.example.taskmanager.dto.RegisterRequestDTO;
import com.example.taskmanager.dto.UserResponseDTO;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository,
                       PasswordEncoder passwordEncoder) {

        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDTO register(RegisterRequestDTO dto) {

        // CHECK IF EMAIL ALREADY EXISTS
        if (repository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // CREATE USER ENTITY
        User user = new User();

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());

        // ENCRYPT PASSWORD
        user.setPassword(
                passwordEncoder.encode(dto.getPassword())
        );

        // SAVE USER
        User savedUser = repository.save(user);

        // CREATE RESPONSE DTO
        UserResponseDTO response = new UserResponseDTO();

        response.setId(savedUser.getId());
        response.setName(savedUser.getName());
        response.setEmail(savedUser.getEmail());

        return response;
    }
}