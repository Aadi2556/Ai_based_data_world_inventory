package com.example.demo.dto;

public class AttendanceSummary {
    private Long   userId;
    private String username;
    private String fullName;
    private String userRole;
    private long   totalDays;
    private long   presentDays;
    private long   absentDays;
    private long   lateDays;
    private long   halfDays;
    private long   leaveDays;
    private double attendanceRate;   // 0–100 %

    public Long   getUserId()                    { return userId; }
    public void   setUserId(Long v)              { userId = v; }
    public String getUsername()                  { return username; }
    public void   setUsername(String v)          { username = v; }
    public String getFullName()                  { return fullName; }
    public void   setFullName(String v)          { fullName = v; }
    public String getUserRole()                  { return userRole; }
    public void   setUserRole(String v)          { userRole = v; }
    public long   getTotalDays()                 { return totalDays; }
    public void   setTotalDays(long v)           { totalDays = v; }
    public long   getPresentDays()               { return presentDays; }
    public void   setPresentDays(long v)         { presentDays = v; }
    public long   getAbsentDays()                { return absentDays; }
    public void   setAbsentDays(long v)          { absentDays = v; }
    public long   getLateDays()                  { return lateDays; }
    public void   setLateDays(long v)            { lateDays = v; }
    public long   getHalfDays()                  { return halfDays; }
    public void   setHalfDays(long v)            { halfDays = v; }
    public long   getLeaveDays()                 { return leaveDays; }
    public void   setLeaveDays(long v)           { leaveDays = v; }
    public double getAttendanceRate()            { return attendanceRate; }
    public void   setAttendanceRate(double v)    { attendanceRate = v; }
}