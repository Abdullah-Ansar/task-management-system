package com.example.taskmanager.controller;

import com.example.taskmanager.dto.*;
import com.example.taskmanager.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.taskmanager.dto.ResetPasswordDTO;
import jakarta.validation.Valid;
import com.example.taskmanager.ratelimit.LoginRateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService service;

    private final LoginRateLimiter loginRateLimiter;

    public AuthController(UserService service,LoginRateLimiter loginRateLimiter) {
        this.service = service;
        this.loginRateLimiter = loginRateLimiter;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @Valid @RequestBody RegisterRequestDTO dto) {

        return ResponseEntity.ok(
                service.register(dto)
        );
    }
    @PostMapping("/resend-otp")
    public ResponseEntity<String> resendOtp(
            @Valid @RequestBody ResendOtpRequestDTO dto) {

        return ResponseEntity.ok(
                service.resendOtp(dto.getEmail())
        );
    }

    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(
            @Valid @RequestBody VerifyOtpRequestDTO dto) {

        return ResponseEntity.ok(
                service.verifyEmail(dto.getEmail(), dto.getOtp())
        );
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO dto,
            HttpServletRequest request) {

        String ipAddress = request.getRemoteAddr();

        if (!loginRateLimiter.isAllowed(ipAddress)) {
            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "Too many login requests. Please try again later."
            );
        }

        return ResponseEntity.ok(
                service.login(dto)
        );
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequestDTO dto) {

        return ResponseEntity.ok(
                service.forgotPassword(dto.getEmail())
        );
    }
    @PostMapping("/verify-reset-otp")
    public ResponseEntity<ResetPasswordResponseDTO> verifyResetOtp(
            @Valid @RequestBody VerifyResetOtpDTO dto) {

        return ResponseEntity.ok(
                service.verifyResetOtp(dto.getEmail(), dto.getOtp())
        );
    }
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @Valid @RequestBody ResetPasswordDTO dto) {

        return ResponseEntity.ok(
                service.resetPassword(dto)
        );
    }

}