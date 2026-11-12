package com.example.taskmanager.controller;

import com.example.taskmanager.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class EmailTestController {

    private final EmailService emailService;

    public EmailTestController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send-email")
    public ResponseEntity<String> sendTestEmail(
            @RequestParam String email) {

        emailService.sendEmail(
                email,
                "Task Manager - Email Test",
                "Congratulations! Your Gmail SMTP integration is working."
        );

        return ResponseEntity.ok("Test email sent successfully!");
    }
}