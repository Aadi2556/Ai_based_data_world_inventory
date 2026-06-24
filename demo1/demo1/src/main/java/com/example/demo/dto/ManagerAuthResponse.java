package com.example.demo.dto;

public class ManagerAuthResponse {

    private String token;
    private String type = "Bearer";
    private ManagerProfileResponse manager;
    private String message;

    public ManagerAuthResponse() {}

    public ManagerAuthResponse(String token, ManagerProfileResponse manager, String message) {
        this.token   = token;
        this.manager = manager;
        this.message = message;
    }

    public String getToken()                        { return token; }
    public void   setToken(String token)            { this.token = token; }

    public String getType()                         { return type; }
    public void   setType(String type)              { this.type = type; }

    public ManagerProfileResponse getManager()      { return manager; }
    public void setManager(ManagerProfileResponse m){ this.manager = m; }

    public String getMessage()                      { return message; }
    public void   setMessage(String message)        { this.message = message; }
}