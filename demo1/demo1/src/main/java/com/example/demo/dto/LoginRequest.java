package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    private String otp;

    public String getUsername()              { return username; }
    public void   setUsername(String u)      { this.username = u; }
    public String getPassword()              { return password; }
    public void   setPassword(String p)      { this.password = p; }
    public String getOtp()                   { return otp; }
    public void   setOtp(String otp)         { this.otp = otp; }
}