package com.example.demo.dto;

public class AttendanceMarkRequest {
    private String status;   // ABSENT | ON_LEAVE | HALF_DAY | PRESENT | LATE
    private String date;     // yyyy-MM-dd  (admin back-fill; self = today)
    private String notes;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}