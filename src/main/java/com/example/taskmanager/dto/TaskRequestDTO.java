package com.example.taskmanager.dto;

import jakarta.validation.constraints.*;

public class TaskRequestDTO {

    @NotBlank(message = "Title is required")
    private String title;

    @Size(max = 1000)
    private String description;

    private boolean completed;

    @NotNull(message = "Status is required")
    private String status;

    // getters & setters


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}