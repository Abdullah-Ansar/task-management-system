package com.example.taskmanager.service;

import com.example.taskmanager.dto.TaskRequestDTO;
import com.example.taskmanager.dto.TaskResponseDTO;
import com.example.taskmanager.entity.Status;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.exception.AccessDeniedException;
import com.example.taskmanager.exception.ResourceNotFoundException;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;

@Service
public class TaskService {

    private static final Logger logger =
            LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository repository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository repository,
                       UserRepository userRepository) {

        this.repository = repository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }
    private Task getTaskOwnedByCurrentUser(Long id) {

        Task task = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Task not found with id: " + id));

        User currentUser = getCurrentUser();
        if (task.getUser() == null) {
            throw new ResourceNotFoundException("Task has no owner.");
        }
        if (!task.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not allowed to access this task.");
        }

        return task;
    }
    public TaskResponseDTO createTask(TaskRequestDTO dto) {

        // Get logged-in user's email from JWT
        // Find user in database
        User user = getCurrentUser();

        // Create task
        Task task = mapToEntity(dto);

        // Assign task to current user
        task.setUser(user);

        // Save task
        Task saved = repository.save(task);


        logger.info(
                "Task '{}' created by user {}",
                saved.getTitle(),
                user.getEmail()
        );

        return mapToDTO(saved);
    }

    public Page<TaskResponseDTO> getAllTasks(
            int page,
            int size,
            String sortBy) {
        User user = getCurrentUser();
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortBy)
        );

        return repository.findByUser(user, pageable)
                .map(this::mapToDTO);
    }

    public TaskResponseDTO getTaskById(Long id) {
        Task task = getTaskOwnedByCurrentUser(id);

        return mapToDTO(task);
    }
    public TaskResponseDTO updateTask(Long id, TaskRequestDTO dto) {
        Task task = getTaskOwnedByCurrentUser(id);

        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setCompleted(dto.isCompleted());
        task.setStatus(Status.valueOf(dto.getStatus()));

        Task updated = repository.save(task);

        return mapToDTO(updated);
    }
    public void deleteTask(Long id) {

        Task task = getTaskOwnedByCurrentUser(id);

        repository.delete(task);
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