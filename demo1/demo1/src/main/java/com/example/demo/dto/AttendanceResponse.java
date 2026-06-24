package com.example.demo.dto;

public class AttendanceResponse {
    private Long   id;
    private String userRole;
    private Long   userId;
    private String username;
    private String fullName;
    private String attendanceDate;
    private String clockIn;
    private String clockOut;
    private String status;
    private String notes;
    private Double workHours;
    private String createdAt;
    private String updatedAt;

    public Long   getId()                       { return id; }
    public void   setId(Long id)               { this.id = id; }
    public String getUserRole()                 { return userRole; }
    public void   setUserRole(String v)         { userRole = v; }
    public Long   getUserId()                   { return userId; }
    public void   setUserId(Long v)             { userId = v; }
    public String getUsername()                 { return username; }
    public void   setUsername(String v)         { username = v; }
    public String getFullName()                 { return fullName; }
    public void   setFullName(String v)         { fullName = v; }
    public String getAttendanceDate()           { return attendanceDate; }
    public void   setAttendanceDate(String v)   { attendanceDate = v; }
    public String getClockIn()                  { return clockIn; }
    public void   setClockIn(String v)          { clockIn = v; }
    public String getClockOut()                 { return clockOut; }
    public void   setClockOut(String v)         { clockOut = v; }
    public String getStatus()                   { return status; }
    public void   setStatus(String v)           { status = v; }
    public String getNotes()                    { return notes; }
    public void   setNotes(String v)            { notes = v; }
    public Double getWorkHours()                { return workHours; }
    public void   setWorkHours(Double v)        { workHours = v; }
    public String getCreatedAt()                { return createdAt; }
    public void   setCreatedAt(String v)        { createdAt = v; }
    public String getUpdatedAt()                { return updatedAt; }
    public void   setUpdatedAt(String v)        { updatedAt = v; }
}