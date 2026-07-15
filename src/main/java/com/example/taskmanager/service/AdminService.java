package com.example.taskmanager.service;

import com.example.taskmanager.dto.TaskResponseDTO;
import com.example.taskmanager.dto.UserResponseDTO;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.exception.ResourceNotFoundException;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class AdminService {
    private static final Logger logger =
            LoggerFactory.getLogger(AdminService.class);
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    private User getCurrentUser() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }
    public AdminService(UserRepository userRepository,
                        TaskRepository taskRepository) {

        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    // User APIs
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapUserToDTO)
                .toList();
    }

    // Task APIs
    public List<TaskResponseDTO> getAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(this::mapTaskToDTO)
                .toList();
    }

    // User mapper
    private UserResponseDTO mapUserToDTO(User user) {

        UserResponseDTO dto = new UserResponseDTO();

        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().name());

        return dto;
    }

    // ⭐ Add this method here
    private TaskResponseDTO mapTaskToDTO(Task task) {

        TaskResponseDTO dto = new TaskResponseDTO();

        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setCompleted(task.isCompleted());
        dto.setStatus(task.getStatus().name());

        return dto;
    }
    public void deleteAnyTask(Long id) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Task not found with id: " + id));
        User admin = getCurrentUser();

        logger.warn(
                "Admin '{}' deleted task '{}' (ID={})",
                admin.getEmail(),
                task.getTitle(),
                task.getId()
        );

        taskRepository.delete(task);
    }
}