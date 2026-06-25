package com.example.taskmanager.service;

import com.example.taskmanager.dto.TaskRequestDTO;
import com.example.taskmanager.dto.TaskResponseDTO;
import com.example.taskmanager.entity.Status;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.exception.ResourceNotFoundException;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository repository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository repository,
                       UserRepository userRepository) {

        this.repository = repository;
        this.userRepository = userRepository;
    }

    public TaskResponseDTO createTask(TaskRequestDTO dto) {

        // Get logged-in user's email from JWT
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        // Find user in database
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // Create task
        Task task = mapToEntity(dto);

        // Assign task to current user
        task.setUser(user);

        // Save task
        Task saved = repository.save(task);

        return mapToDTO(saved);
    }

    public Page<TaskResponseDTO> getAllTasks(
            int page,
            int size,
            String sortBy) {
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortBy)
        );

        return repository.findByUser(user, pageable)
                .map(this::mapToDTO);
    }

    public TaskResponseDTO getTaskById(Long id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        return mapToDTO(task);
    }
    public TaskResponseDTO updateTask(Long id, TaskRequestDTO dto) {
        Task existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        existing.setTitle(dto.getTitle());
        existing.setDescription(dto.getDescription());
        existing.setCompleted(dto.isCompleted());
        existing.setStatus(Status.valueOf(dto.getStatus()));

        Task updated = repository.save(existing);

        return mapToDTO(updated);
    }
    public void deleteTask(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Task not found with id: " + id);
        }
        repository.deleteById(id);
    }
    private Task mapToEntity(TaskRequestDTO dto) {
        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setCompleted(dto.isCompleted());
        task.setStatus(Status.valueOf(dto.getStatus())); // IMPORTANT

        return task;
    }
    private TaskResponseDTO mapToDTO(Task task) {
        TaskResponseDTO dto = new TaskResponseDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setCompleted(task.isCompleted());
        dto.setStatus(task.getStatus().name());

        return dto;
    }
    public List<TaskResponseDTO> getTasksByStatus(String status) {

        Status taskStatus = Status.valueOf(status);

        return repository.findByStatus(taskStatus)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }
    public List<TaskResponseDTO> searchTasks(String keyword) {

        return repository
                .findByTitleContainingIgnoreCase(keyword)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }
}