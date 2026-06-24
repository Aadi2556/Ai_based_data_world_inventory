package com.example.demo.dto;

public class TechnicianAuthResponse {
    private String token;
    private String type = "Bearer";
    private TechnicianProfileResponse technician;
    private String message;

    public TechnicianAuthResponse() {}
    public TechnicianAuthResponse(String token, TechnicianProfileResponse technician, String message) {
        this.token = token; this.technician = technician; this.message = message;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public TechnicianProfileResponse getTechnician() { return technician; }
    public void setTechnician(TechnicianProfileResponse t) { this.technician = t; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}