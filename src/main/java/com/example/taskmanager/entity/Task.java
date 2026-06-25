package com.example.taskmanager.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;



@Entity
@Table(name = "tasks")
public class Task {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @Size(max = 1000, message = "Description too long")
    private String description;

    private boolean completed;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    private Status status;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Constructors
    public Task() {}

    public Task(String title, String description, boolean completed, Status status) {
        this.title = title;
        this.description = description;
        this.completed = completed;
        this.status = status;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}

