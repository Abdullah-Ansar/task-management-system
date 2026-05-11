package com.example.taskmanager.repository;
import java.util.List;
import com.example.taskmanager.entity.Status;
import com.example.taskmanager.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByStatus(Status status);
    List<Task> findByTitleContainingIgnoreCase(String keyword);
}