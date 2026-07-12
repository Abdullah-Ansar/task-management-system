package com.example.taskmanager.controller;

import com.example.taskmanager.dto.TaskResponseDTO;
import com.example.taskmanager.dto.UserResponseDTO;
import com.example.taskmanager.service.AdminService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService service;

    public AdminController(AdminService service) {
        this.service = service;
    }
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponseDTO> getAllUsers() {

        return service.getAllUsers();
    }
    @GetMapping("/tasks")
    @PreAuthorize("hasRole('ADMIN')")
    public List<TaskResponseDTO> getAllTasks() {

        return service.getAllTasks();
    }

}