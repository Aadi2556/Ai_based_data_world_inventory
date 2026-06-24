package com.example.demo.dto;

public class AuthResponse {

    private String token;
    private String type = "Bearer";
    private AdminProfileResponse admin;
    private String message;

    public AuthResponse() {}

    public AuthResponse(String token, AdminProfileResponse admin, String message) {
        this.token   = token;
        this.admin   = admin;
        this.message = message;
    }

    public String getToken()                    { return token; }
    public void   setToken(String token)        { this.token = token; }

    public String getType()                     { return type; }
    public void   setType(String type)          { this.type = type; }

    public AdminProfileResponse getAdmin()      { return admin; }
    public void setAdmin(AdminProfileResponse a){ this.admin = a; }

    public String getMessage()                  { return message; }
    public void   setMessage(String message)    { this.message = message; }
}