package com.example.taskmanager.service;

import com.example.taskmanager.dto.LoginRequestDTO;
import com.example.taskmanager.dto.LoginResponseDTO;
import com.example.taskmanager.dto.RegisterRequestDTO;
import com.example.taskmanager.dto.UserResponseDTO;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.security.JwtService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository repository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {

        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
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
    public LoginResponseDTO login(LoginRequestDTO dto) {

        User user = repository.findByEmail(dto.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Invalid email or password"));

        boolean passwordMatches =
                passwordEncoder.matches(
                        dto.getPassword(),
                        user.getPassword()
                );

        if (!passwordMatches) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtService.generateToken(user.getEmail());

        return new LoginResponseDTO(token);
    }
}