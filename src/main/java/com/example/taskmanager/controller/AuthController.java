package com.example.taskmanager.controller;

import com.example.taskmanager.dto.*;
import com.example.taskmanager.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.taskmanager.dto.ResetPasswordDTO;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService service;

    public AuthController(UserService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody RegisterRequestDTO dto) {

        return ResponseEntity.ok(
                service.register(dto)
        );
    }
    @PostMapping("/resend-otp")
    public ResponseEntity<String> resendOtp(
            @RequestBody ResendOtpRequestDTO dto) {

        return ResponseEntity.ok(
                service.resendOtp(dto.getEmail())
        );
    }

    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(
            @RequestBody VerifyOtpRequestDTO dto) {

        return ResponseEntity.ok(
                service.verifyEmail(dto.getEmail(), dto.getOtp())
        );
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody LoginRequestDTO dto) {

        return ResponseEntity.ok(
                service.login(dto)
        );
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @RequestBody ForgotPasswordRequestDTO dto) {

        return ResponseEntity.ok(
                service.forgotPassword(dto.getEmail())
        );
    }
    @PostMapping("/verify-reset-otp")
    public ResponseEntity<ResetPasswordResponseDTO> verifyResetOtp(
            @RequestBody VerifyResetOtpDTO dto) {

        return ResponseEntity.ok(
                service.verifyResetOtp(dto.getEmail(), dto.getOtp())
        );
    }
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestBody ResetPasswordDTO dto) {

        return ResponseEntity.ok(
                service.resetPassword(dto)
        );
    }

}