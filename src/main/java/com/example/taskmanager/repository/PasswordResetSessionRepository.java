
package com.example.taskmanager.repository;

import com.example.taskmanager.entity.PasswordResetSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetSessionRepository
        extends JpaRepository<PasswordResetSession, Long> {

    Optional<PasswordResetSession> findByEmail(String email);

    void deleteByEmail(String email);
}
