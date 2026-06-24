package com.example.demo.dto;

public class StaffOperatorAuthResponse {

    private String token;
    private String type = "Bearer";
    private StaffOperatorProfileResponse staff;
    private String message;

    public StaffOperatorAuthResponse() {}

    public StaffOperatorAuthResponse(String token, StaffOperatorProfileResponse staff, String message) {
        this.token   = token;
        this.staff   = staff;
        this.message = message;
    }

    public String getToken()                              { return token; }
    public void   setToken(String token)                  { this.token = token; }

    public String getType()                               { return type; }
    public void   setType(String type)                    { this.type = type; }

    public StaffOperatorProfileResponse getStaff()        { return staff; }
    public void setStaff(StaffOperatorProfileResponse s)  { this.staff = s; }

    public String getMessage()                            { return message; }
    public void   setMessage(String message)              { this.message = message; }
}