package com.example.taskmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.taskmanager.exception.InvalidCredentialsException;

import java.util.HashMap;
import java.util.Map;
import com.example.taskmanager.exception.RateLimitException;
import com.example.taskmanager.exception.InvalidOtpException;
import com.example.taskmanager.exception.AccountLockedException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<Map<String, String>> handleRateLimit(
            RateLimitException ex) {

        logger.warn("Rate limit exceeded: {}", ex.getMessage());

        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getMessage());
        error.put("status", "429");

        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(error);
    }

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<Map<String, String>> handleAccountLocked(
            AccountLockedException ex) {

        logger.warn("Account locked: {}", ex.getMessage());

        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getMessage());
        error.put("status", "423");

        return ResponseEntity
                .status(HttpStatus.LOCKED)
                .body(error);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleInvalidCredentials(
            InvalidCredentialsException ex) {

        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getMessage());
        error.put("status", "401");

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(error);
    }

    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<Map<String, String>> handleInvalidOtp(
            InvalidOtpException ex) {

        logger.warn("OTP verification failed: {}", ex.getMessage());

        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getMessage());
        error.put("status", "400");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(ResourceNotFoundException ex) {

        logger.warn("{}", ex.getMessage());

        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getMessage());
        error.put("status", "404");

        return error;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidation(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return errors;
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDenied(
            AccessDeniedException ex) {
        logger.warn("Access denied: {}", ex.getMessage());
        logger.warn("Validation failed");
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleGeneralException(Exception ex) {

        logger.error("Unexpected server error", ex);

        Map<String, String> error = new HashMap<>();

        error.put("message", "Something went wrong. Please try again later.");
        error.put("status", "500");

        return error;
    }

}