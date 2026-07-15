package com.example.taskmanager.controller;

import com.example.taskmanager.dto.TaskResponseDTO;
import com.example.taskmanager.dto.UserResponseDTO;
import com.example.taskmanager.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    @DeleteMapping("/tasks/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAnyTask(
            @PathVariable Long id) {

        service.deleteAnyTask(id);

        return ResponseEntity.noContent().build();
    }

}