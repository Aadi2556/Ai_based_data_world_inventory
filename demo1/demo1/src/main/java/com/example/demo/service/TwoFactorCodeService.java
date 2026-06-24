package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TwoFactorCodeService {

    private static class Entry {
        final String code;
        final long expiresAtEpochSeconds;
        Entry(String code, long expiresAtEpochSeconds) {
            this.code = code;
            this.expiresAtEpochSeconds = expiresAtEpochSeconds;
        }
    }

    private final SecureRandom random = new SecureRandom();
    private final Map<String, Entry> store = new ConcurrentHashMap<>();

    public String generateAndStore(String userType, String username) {
        String code = String.format("%06d", random.nextInt(1_000_000));
        long expires = Instant.now().plusSeconds(5 * 60).getEpochSecond();
        store.put(key(userType, username), new Entry(code, expires));
        return code;
    }

    public void validate(String userType, String username, String providedCode) {
        Entry e = store.get(key(userType, username));
        if (e == null) {
            throw new RuntimeException("2FA code not found. Please request a new code.");
        }
        if (Instant.now().getEpochSecond() > e.expiresAtEpochSeconds) {
            store.remove(key(userType, username));
            throw new RuntimeException("2FA code expired. Please request a new code.");
        }
        if (providedCode == null || !providedCode.trim().equals(e.code)) {
            throw new RuntimeException("Invalid 2FA code");
        }
        // one-time use
        store.remove(key(userType, username));
    }

    private String key(String userType, String username) {
        return (userType == null ? "" : userType.toUpperCase()) + ":" + (username == null ? "" : username.toLowerCase());
    }
}

