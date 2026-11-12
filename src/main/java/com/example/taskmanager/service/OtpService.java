
package com.example.taskmanager.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class OtpService {

    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    public OtpService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    // Generate a secure 6-digit OTP
    public String generateOtp() {
        int otp = secureRandom.nextInt(100000, 1000000);
        return String.valueOf(otp);
    }

    // Hash OTP before storing it in database
    public String hashOtp(String otp) {
        return passwordEncoder.encode(otp);
    }

    // Compare entered OTP with stored hash
    public boolean verifyOtp(String enteredOtp, String storedHash) {
        return passwordEncoder.matches(enteredOtp, storedHash);
    }
}
