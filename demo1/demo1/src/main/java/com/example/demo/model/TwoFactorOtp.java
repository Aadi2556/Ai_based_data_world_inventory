package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "two_factor_otp")
public class TwoFactorOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    // e.g., "ADMIN", "MANAGER", "TECHNICIAN", "STAFF"
    private String userType;

    private String otpHash;

    private LocalDateTime expiryDate;

    private LocalDateTime createdAt;

    // Default constructor
    public TwoFactorOtp() {}

    public TwoFactorOtp(String username, String userType, String otpHash, LocalDateTime expiryDate, LocalDateTime createdAt) {
        this.username = username;
        this.userType = userType;
        this.otpHash = otpHash;
        this.expiryDate = expiryDate;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public String getOtpHash() { return otpHash; }
    public void setOtpHash(String otpHash) { this.otpHash = otpHash; }

    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

