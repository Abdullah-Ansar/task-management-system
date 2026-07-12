package com.example.taskmanager.service;

import com.example.taskmanager.dto.TaskResponseDTO;
import com.example.taskmanager.dto.UserResponseDTO;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class AdminService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

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
}