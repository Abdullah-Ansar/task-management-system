package com.example.taskmanager.service;

import com.example.taskmanager.dto.LoginRequestDTO;
import com.example.taskmanager.dto.LoginResponseDTO;
import com.example.taskmanager.entity.Role;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;
    @Test
    void login_ShouldReturnToken_WhenCredentialsAreValid() {
        LoginRequestDTO request = new LoginRequestDTO();

        request.setEmail("abdullah@gmail.com");
        request.setPassword("password123");

        User user = new User();

        user.setEmail("abdullah@gmail.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.USER);

        when(repository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()))
                .thenReturn(true);
        when(jwtService.generateToken(user.getEmail()))
                .thenReturn("fake-jwt-token");

        LoginResponseDTO response = userService.login(request);

        assertEquals("fake-jwt-token", response.getToken());
    }
    @Test
    void login_ShouldThrowException_WhenUserNotFound() {

        // Arrange
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("abdullah@gmail.com");
        request.setPassword("password123");

        when(repository.findByEmail(request.getEmail()))
                .thenReturn(Optional.empty());

        // Act + Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.login(request)
        );

        assertEquals(
                "Invalid email or password",
                exception.getMessage()
        );
    }

}