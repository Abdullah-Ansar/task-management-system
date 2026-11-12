package com.example.taskmanager.service;

import com.example.taskmanager.dto.*;
import com.example.taskmanager.entity.Role;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.exception.InvalidCredentialsException;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.security.JwtService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.taskmanager.entity.EmailVerification;
import com.example.taskmanager.repository.EmailVerificationRepository;
import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import com.example.taskmanager.exception.RateLimitException;
import com.example.taskmanager.exception.InvalidOtpException;
import com.example.taskmanager.entity.PasswordResetToken;
import com.example.taskmanager.repository.PasswordResetTokenRepository;
import com.example.taskmanager.entity.PasswordResetSession;
import com.example.taskmanager.repository.PasswordResetSessionRepository;
import com.example.taskmanager.exception.AccountLockedException;
import java.time.Duration;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class UserService {

    private static final Logger logger =
            LoggerFactory.getLogger(UserService.class);

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final OtpService otpService;
    private final EmailVerificationRepository verificationRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordResetSessionRepository passwordResetSessionRepository;

    public UserService(
            UserRepository repository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            EmailService emailService,
            OtpService otpService,
            EmailVerificationRepository verificationRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            PasswordResetSessionRepository passwordResetSessionRepository
            ) {

        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.otpService = otpService;
        this.verificationRepository = verificationRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordResetSessionRepository = passwordResetSessionRepository;
    }
    public String resendOtp(String email) {

        email = email.trim().toLowerCase();

        User user = repository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found. Please register first."));

        if (user.isEmailVerified()) {
            throw new RuntimeException("Email is already verified.");
        }

        EmailVerification verification =
                verificationRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException("OTP record not found. Please register again."));

        LocalDateTime now = LocalDateTime.now();

        long secondsSinceLastSent =
                Duration.between(verification.getLastSentAt(), now).getSeconds();

        if (secondsSinceLastSent < 60) {
            throw new RateLimitException(
                    "Please wait " + (60 - secondsSinceLastSent)
                            + " seconds before requesting another OTP."
            );
        }

        String otp = otpService.generateOtp();

        verification.setOtpHash(otpService.hashOtp(otp));
        verification.setExpiresAt(now.plusMinutes(10));
        verification.setAttempts(0);
        verification.setLastSentAt(now);

        verificationRepository.save(verification);

        emailService.sendEmail(
                email,
                "Task Manager - New Verification OTP",
                "Hello " + user.getName() + ",\n\n"
                        + "Your new verification OTP is: " + otp
                        + "\n\nThis OTP expires in 10 minutes."
        );

        return "A new verification OTP has been sent to your email.";
    }


    @Transactional
    public ResetPasswordResponseDTO verifyResetOtp(
            String email, String enteredOtp) {

        email = email.trim().toLowerCase();

        PasswordResetToken resetToken =
                passwordResetTokenRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new InvalidOtpException("Reset OTP not found. Request a new OTP."));

        if (LocalDateTime.now().isAfter(resetToken.getExpiresAt())) {
            passwordResetTokenRepository.delete(resetToken);

            throw new InvalidOtpException("Reset OTP expired. Request a new OTP.");
        }

        if (resetToken.getAttempts() >= 5) {
            passwordResetTokenRepository.delete(resetToken);

            throw new InvalidOtpException("Maximum OTP attempts exceeded. Request a new OTP.");
        }

        if (!otpService.verifyOtp(enteredOtp, resetToken.getOtpHash())) {

            int attempts = resetToken.getAttempts() + 1;
            resetToken.setAttempts(attempts);

            if (attempts >= 5) {
                passwordResetTokenRepository.delete(resetToken);

                throw new InvalidOtpException(
                        "Maximum OTP attempts exceeded. Request a new OTP."
                );
            }

            passwordResetTokenRepository.save(resetToken);

            throw new InvalidOtpException(
                    "Invalid OTP. Attempts remaining: " + (5 - attempts)
            );
        }

        // Generate a secure reset token
        SecureRandom random = new SecureRandom();
        byte[] tokenBytes = new byte[32];
        random.nextBytes(tokenBytes);

        String rawToken = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(tokenBytes);

        // Invalidate any previous reset session
        passwordResetSessionRepository.deleteByEmail(email);

        PasswordResetSession session = new PasswordResetSession();
        session.setEmail(email);
        session.setTokenHash(passwordEncoder.encode(rawToken));
        session.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        session.setUsed(false);

        passwordResetSessionRepository.save(session);

        // OTP can no longer be reused
        passwordResetTokenRepository.delete(resetToken);

        return new ResetPasswordResponseDTO(rawToken);
    }


    @Transactional
    public String resetPassword(ResetPasswordDTO dto) {

        String email = dto.getEmail().trim().toLowerCase();

        PasswordResetSession session =
                passwordResetSessionRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException("Invalid or expired reset session."));

        // Check token expiry
        if (LocalDateTime.now().isAfter(session.getExpiresAt())) {
            passwordResetSessionRepository.delete(session);
            throw new RuntimeException("Reset session expired. Please request a new OTP.");
        }

        // Check whether token was already used
        if (session.isUsed()) {
            throw new RuntimeException("Reset token has already been used.");
        }

        // Verify reset token
        if (!passwordEncoder.matches(
                dto.getResetToken(),
                session.getTokenHash())) {

            throw new RuntimeException("Invalid reset token.");
        }

        // Find user
        User user = repository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found."));

        // Encrypt new password
        user.setPassword(
                passwordEncoder.encode(dto.getNewPassword())
        );

        // Invalidate all previously issued JWT tokens
        user.setTokenVersion(user.getTokenVersion() + 1);

        // Reset account lockout
        user.setFailedLoginAttempts(0);
        user.setLockTime(null);

        repository.save(user);

        // Invalidate reset session
        session.setUsed(true);
        passwordResetSessionRepository.delete(session);

        return "Password reset successfully. Please log in with your new password.";
    }


    @Transactional
    public String register(RegisterRequestDTO dto) {

        String email = dto.getEmail().trim().toLowerCase();

        User user = repository.findByEmail(email).orElse(null);

        // Existing verified account
        if (user != null && user.isEmailVerified()) {
            throw new RuntimeException("Email already registered");
        }

        // Create account if it does not already exist
        if (user == null) {
            user = new User();

            user.setName(dto.getName());
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            user.setRole(Role.USER);
            user.setEmailVerified(false);

            repository.save(user);
        }

        // Generate OTP
        String otp = otpService.generateOtp();

        // Find existing verification record or create a new one
        EmailVerification verification =
                verificationRepository.findByEmail(email)
                        .orElseGet(EmailVerification::new);

        verification.setEmail(email);
        verification.setOtpHash(otpService.hashOtp(otp));
        verification.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        verification.setAttempts(0);
        verification.setLastSentAt(LocalDateTime.now());

        verificationRepository.save(verification);

        // Send OTP
        emailService.sendEmail(
                email,
                "Task Manager - Email Verification",
                "Hello " + user.getName() + ",\n\n"
                        + "Your email verification OTP is: " + otp
                        + "\n\nThis OTP expires in 10 minutes."
        );

        return "Registration initiated. Please check your email for the OTP.";
    }


    public String forgotPassword(String email) {

        email = email.trim().toLowerCase();

        User user = repository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("If the account exists, a reset OTP will be sent."));

        if (!user.isEmailVerified()) {
            throw new RuntimeException("Please verify your email first.");
        }

        LocalDateTime now = LocalDateTime.now();

        PasswordResetToken resetToken =
                passwordResetTokenRepository.findByEmail(email)
                        .orElseGet(PasswordResetToken::new);

        // Enforce 60-second resend cooldown
        if (resetToken.getId() != null) {

            long secondsSinceLastSent =
                    Duration.between(resetToken.getLastSentAt(), now).getSeconds();

            if (secondsSinceLastSent < 60) {
                throw new RateLimitException(
                        "Please wait " + (60 - secondsSinceLastSent)
                                + " seconds before requesting another reset OTP."
                );
            }
        }

        String otp = otpService.generateOtp();

        resetToken.setEmail(email);
        resetToken.setOtpHash(otpService.hashOtp(otp));
        resetToken.setExpiresAt(now.plusMinutes(10));
        resetToken.setAttempts(0);
        resetToken.setLastSentAt(now);

        passwordResetTokenRepository.save(resetToken);

        emailService.sendEmail(
                email,
                "Task Manager - Password Reset OTP",
                "Hello " + user.getName() + ",\n\n"
                        + "Your password reset OTP is: " + otp
                        + "\n\nThis OTP expires in 10 minutes."
        );

        return "If the account exists, a password reset OTP will be sent.";
    }

    @Transactional(noRollbackFor = InvalidOtpException.class)
    public String verifyEmail(String email, String enteredOtp) {

        email = email.trim().toLowerCase();

        // Find OTP record
        EmailVerification verification =
                verificationRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException("OTP not found. Please register again."));

        // Check whether OTP has expired
        if (LocalDateTime.now().isAfter(verification.getExpiresAt())) {
            verificationRepository.delete(verification);
            throw new RuntimeException("OTP expired. Please request a new OTP.");
        }

        // Check maximum attempts
        if (verification.getAttempts() >= 5) {
            verificationRepository.delete(verification);
            throw new RuntimeException("Maximum OTP attempts exceeded. Request a new OTP.");
        }

        // Verify OTP

        if (!otpService.verifyOtp(enteredOtp, verification.getOtpHash())) {

            int attempts = verification.getAttempts() + 1;

            verification.setAttempts(attempts);

            if (attempts >= 5) {
                verificationRepository.delete(verification);

                throw new InvalidOtpException(
                        "Maximum OTP attempts exceeded. Please request a new OTP."
                );
            }

            verificationRepository.save(verification);

            throw new InvalidOtpException(
                    "Invalid OTP. Attempts remaining: " + (5 - attempts)
            );
        }


        // Find user
        User user = repository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found. Please register again."));

        // Activate account
        user.setEmailVerified(true);
        repository.save(user);

        // Invalidate OTP
        verificationRepository.delete(verification);

        return "Email verified successfully. You can now log in.";
    }



    @Transactional(noRollbackFor = {
            AccountLockedException.class,
            InvalidCredentialsException.class
    })
    public LoginResponseDTO login(LoginRequestDTO dto) {

        String email = dto.getEmail().trim().toLowerCase();

        User user = repository.findByEmail(email)
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Invalid email or password."
                        ));

        LocalDateTime now = LocalDateTime.now();

        // Check whether account is currently locked
        if (user.getLockTime() != null) {

            LocalDateTime unlockTime =
                    user.getLockTime().plusMinutes(15);

            if (now.isBefore(unlockTime)) {

                long remainingSeconds =
                        Duration.between(now, unlockTime).getSeconds();

                long remainingMinutes =
                        (remainingSeconds + 59) / 60;

                throw new AccountLockedException(
                        "Account temporarily locked. Try again in "
                                + remainingMinutes + " minute(s)."
                );
            }

            // Lock period expired: automatically unlock
            user.setLockTime(null);
            user.setFailedLoginAttempts(0);
        }

        // Check password
        boolean passwordMatches = passwordEncoder.matches(
                dto.getPassword(),
                user.getPassword()
        );

        if (!passwordMatches) {

            int attempts = user.getFailedLoginAttempts() + 1;

            user.setFailedLoginAttempts(attempts);

            if (attempts >= 3) {

                user.setLockTime(now);

                repository.save(user);

                throw new AccountLockedException(
                        "Too many failed login attempts. "
                                + "Your account is locked for 15 minutes."
                );
            }

            repository.save(user);

            throw new InvalidCredentialsException(
                    "Invalid email or password. Attempts remaining: "
                            + (3 - attempts)
            );
        }

        // Require verified email
        if (!user.isEmailVerified()) {
            throw new InvalidCredentialsException(
                    "Please verify your email before logging in."
            );
        }

        // Reset failed attempts after successful login
        user.setFailedLoginAttempts(0);
        user.setLockTime(null);

        repository.save(user);

        // Generate JWT
        String token = jwtService.generateToken(
                user.getEmail(),
                user.getTokenVersion()
        );

        logger.info("User '{}' logged in successfully", user.getEmail());

        return new LoginResponseDTO(token);
    }

}