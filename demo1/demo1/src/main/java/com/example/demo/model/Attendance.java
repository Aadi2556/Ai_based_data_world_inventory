package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Attendance record for a single day per user.
 *
 * userRole: "TECHNICIAN" | "STAFF" | "ADMIN" | "MANAGER"
 * userId  : the PK from the corresponding role table
 * status  : PRESENT | ABSENT | LATE | HALF_DAY | ON_LEAVE
 *
 * clockIn / clockOut are stored as LocalDateTime so the date
 * component is always consistent with attendanceDate.
 */
@Entity
@Table(
        name = "attendance",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_attendance_user_date",
                        columnNames = { "user_role", "user_id", "attendance_date" }
                )
        }
)
public class Attendance {

    public enum Status {
        PRESENT, ABSENT, LATE, HALF_DAY, ON_LEAVE
    }

    public enum UserRole {
        TECHNICIAN, STAFF, ADMIN, MANAGER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Role of the user who owns this record. */
    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false, length = 20)
    private UserRole userRole;

    /** PK from technicians, staff_operators, admins, or managers table. */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** Denormalised for easy reporting without joins. */
    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    /** The calendar date this record covers. */
    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    /** Actual clock-in timestamp (may be null if ABSENT / ON_LEAVE). */
    @Column(name = "clock_in")
    private LocalDateTime clockIn;

    /** Actual clock-out timestamp (null until the user clocks out). */
    @Column(name = "clock_out")
    private LocalDateTime clockOut;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Status status = Status.PRESENT;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /** Total working hours — computed when clocking out. */
    @Column(name = "work_hours")
    private Double workHours;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Attendance() {}

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UserRole getUserRole() { return userRole; }
    public void setUserRole(UserRole userRole) { this.userRole = userRole; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public LocalDate getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(LocalDate attendanceDate) { this.attendanceDate = attendanceDate; }

    public LocalDateTime getClockIn() { return clockIn; }
    public void setClockIn(LocalDateTime clockIn) { this.clockIn = clockIn; }

    public LocalDateTime getClockOut() { return clockOut; }
    public void setClockOut(LocalDateTime clockOut) { this.clockOut = clockOut; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Double getWorkHours() { return workHours; }
    public void setWorkHours(Double workHours) { this.workHours = workHours; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}