package com.example.demo.repository;

import com.example.demo.model.Attendance;
import com.example.demo.model.Attendance.UserRole;
import com.example.demo.model.Attendance.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    /* ── Single record lookup ── */
    Optional<Attendance> findByUserRoleAndUserIdAndAttendanceDate(
            UserRole userRole, Long userId, LocalDate date);

    /* ── All records for one user ── */
    List<Attendance> findByUserRoleAndUserIdOrderByAttendanceDateDesc(
            UserRole userRole, Long userId);

    /* ── Records for one user in a date range ── */
    List<Attendance> findByUserRoleAndUserIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
            UserRole userRole, Long userId, LocalDate from, LocalDate to);

    /* ── All records for a given date (admin daily view) ── */
    List<Attendance> findByAttendanceDateOrderByUserRoleAscFullNameAsc(LocalDate date);

    /* ── All records for a role in a date range (admin report) ── */
    List<Attendance> findByUserRoleAndAttendanceDateBetweenOrderByAttendanceDateDescFullNameAsc(
            UserRole userRole, LocalDate from, LocalDate to);

    /* ── All records in date range (both roles) ── */
    List<Attendance> findByAttendanceDateBetweenOrderByAttendanceDateDescUserRoleAscFullNameAsc(
            LocalDate from, LocalDate to);

    /* ── Count helpers for summary stats ── */
    long countByUserRoleAndUserIdAndStatus(UserRole userRole, Long userId, Status status);

    long countByUserRoleAndUserIdAndAttendanceDateBetween(
            UserRole userRole, Long userId, LocalDate from, LocalDate to);

    /* ── Latest record for a user (for dashboard "today" tile) ── */
    @Query("SELECT a FROM Attendance a WHERE a.userRole = :role AND a.userId = :uid " +
            "ORDER BY a.attendanceDate DESC")
    List<Attendance> findLatestByUser(@Param("role") UserRole role,
                                      @Param("uid")  Long userId);
// In AttendanceRepository.java — add this method:

    /** Check if a technician was present (PRESENT or LATE) on a given date */
    @Query("SELECT a FROM Attendance a WHERE a.userRole = 'TECHNICIAN' " +
            "AND a.attendanceDate = :date " +
            "AND (a.fullName = :nameOrUsername OR a.username = :nameOrUsername) " +
            "AND (a.status = 'PRESENT' OR a.status = 'LATE')")
    List<Attendance> findPresentTechnicianByNameOrUsername(
            @Param("nameOrUsername") String nameOrUsername,
            @Param("date") LocalDate date);
    /* ── All records for specific username (used in admin lookup) ── */
    List<Attendance> findByUsernameOrderByAttendanceDateDesc(String username);
}