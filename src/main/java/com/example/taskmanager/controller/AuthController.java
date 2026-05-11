package com.example.taskmanager.controller;

import com.example.taskmanager.dto.RegisterRequestDTO;
import com.example.taskmanager.dto.UserResponseDTO;
import com.example.taskmanager.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService service;

    public AuthController(UserService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(
            @RequestBody RegisterRequestDTO dto) {

        return ResponseEntity.ok(
                service.register(dto)
        );
    }
}