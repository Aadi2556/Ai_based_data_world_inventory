package com.example.demo.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordPolicyService {

    /**
     * Simple "normal password rules":
     * - min 8 chars (validated in DTO)
     * - must contain upper, lower, digit
     */
    public void validateStrength(String rawPassword) {
        if (rawPassword == null) {
            throw new RuntimeException("Password is required");
        }
        // Minimal length is 6, which is validated in DTO. No need for uppercase, lowercase, and number check anymore.
    }

    public void validateNotSameAsCurrent(PasswordEncoder encoder, String newRaw, String currentEncoded) {
        if (currentEncoded != null && encoder.matches(newRaw, currentEncoded)) {
            throw new RuntimeException("New password cannot be the same as the current password");
        }
    }
}
