package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service that handles role migration across tables.
 *
 * Design decisions:
 *  - @Transactional lives HERE (service layer), NOT on the controller.
 *    Keeping it on the controller causes Spring to mark the transaction
 *    rollback-only when a RuntimeException is thrown, then throw
 *    UnexpectedRollbackException when the controller's catch block
 *    tries to commit.
 *  - employeeId is only copied when non-null/non-blank to avoid
 *    duplicate unique-key violations across tables.
 *  - A full conflict check (username, email, employeeId) is performed
 *    before any write, so errors surface as clean 400 responses.
 *  - Technicians cannot change role while they have active tasks
 *    (maintenance schedules or repair logs).
 */
@Service
public class RoleChangeService {

    @Autowired private AdminRepository         adminRepo;
    @Autowired private TechnicianRepository    techRepo;
    @Autowired private StaffOperatorRepository staffRepo;
    @Autowired private ManagerRepository       managerRepo;
    @Autowired private MaintenanceScheduleRepository scheduleRepo;   // ADDED
    @Autowired private RepairLogRepository           repairRepo;     // ADDED

    // ── Public API ────────────────────────────────────────────────────────────

    @Transactional
    public String changeRole(String fromRole, Long fromId, String toRole) {
        return migrate(fromRole.toLowerCase().trim(), fromId, toRole.toLowerCase().trim());
    }

    // ── Dispatcher ────────────────────────────────────────────────────────────

    private String migrate(String from, Long id, String to) {
        return switch (from) {
            case "technician" -> migrateFromTechnician(id, to);
            case "staff"      -> migrateFromStaff(id, to);
            case "manager"    -> migrateFromManager(id, to);
            case "admin"      -> migrateFromAdmin(id, to);
            default           -> throw new IllegalArgumentException("Unknown fromRole: " + from);
        };
    }

    // ── GUARD METHOD (active tasks check) ─────────────────────────────────────
    /**
     * Blocks role change if the technician has any active maintenance schedule
     * (SCHEDULED or IN_PROGRESS) or any open/in-progress repair assigned to them.
     *
     * Match is attempted against both "FirstName LastName" and username,
     * the same way TechnicianService.removeFromTask() works.
     */
    private void guardTechnicianHasActiveTasks(Technician t) {
        String nameMatch     = t.getFirstName() + " " + t.getLastName();
        String usernameMatch = t.getUsername();

        long activeSchedules = scheduleRepo.findAllByOrderByScheduledDateDesc().stream()
                .filter(s -> s.getStatus() == MaintenanceSchedule.Status.SCHEDULED
                        || s.getStatus() == MaintenanceSchedule.Status.IN_PROGRESS)
                .filter(s -> nameMatch.equalsIgnoreCase(s.getAssignedTechnician())
                        || usernameMatch.equalsIgnoreCase(s.getAssignedTechnician()))
                .count();

        if (activeSchedules > 0) {
            throw new IllegalArgumentException(
                    "Cannot change role: " + nameMatch + " still has " + activeSchedules
                            + " active maintenance task(s). Please complete or reassign them first.");
        }

        long activeRepairs = repairRepo.findAllByOrderByReportedDateDesc().stream()
                .filter(r -> r.getStatus() == RepairLog.RepairStatus.OPEN
                        || r.getStatus() == RepairLog.RepairStatus.IN_PROGRESS)
                .filter(r -> nameMatch.equalsIgnoreCase(r.getTechnician())
                        || usernameMatch.equalsIgnoreCase(r.getTechnician()))
                .count();

        if (activeRepairs > 0) {
            throw new IllegalArgumentException(
                    "Cannot change role: " + nameMatch + " still has " + activeRepairs
                            + " open repair log(s). Please resolve them first.");
        }
    }

    // ── FROM TECHNICIAN (with guard) ──────────────────────────────────────────

    private String migrateFromTechnician(Long id, String to) {
        Technician t = techRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Technician not found with id: " + id));

        // ── GUARD: block if active tasks exist ────────────────────────────────
        guardTechnicianHasActiveTasks(t);

        // Pre-flight conflict checks (throws IllegalArgumentException on conflict)
        checkUsernameConflict(t.getUsername(), "technician");
        checkEmailConflict(t.getEmail(), "technician");
        checkEmployeeIdConflict(t.getEmployeeId(), "technician");

        switch (to) {
            case "staff" -> {
                StaffOperator s = new StaffOperator();
                copyCommon(s, t.getFirstName(), t.getLastName(), t.getEmail(),
                        t.getPassword(), t.getPhone(), t.getUsername(),
                        t.getEmployeeId(), t.isActive());
                s.setJobTitle(t.getSpecialization());
                s.setExperienceYears(t.getExperienceYears());
                s.setCertifications(t.getCertifications());
                s.setNotes(t.getNotes());
                staffRepo.save(s);
            }
            case "manager" -> {
                Manager m = new Manager();
                copyCommon(m, t.getFirstName(), t.getLastName(), t.getEmail(),
                        t.getPassword(), t.getPhone(), t.getUsername(),
                        t.getEmployeeId(), t.isActive());
                m.setJobTitle(t.getSpecialization());
                m.setExperienceYears(t.getExperienceYears());
                m.setCertifications(t.getCertifications());
                m.setNotes(t.getNotes());
                managerRepo.save(m);
            }
            case "admin" -> {
                Admin a = new Admin();
                copyCommon(a, t.getFirstName(), t.getLastName(), t.getEmail(),
                        t.getPassword(), t.getPhone(), t.getUsername(),
                        t.getEmployeeId(), t.isActive());
                adminRepo.save(a);
            }
            default -> throw new IllegalArgumentException("Unknown toRole: " + to);
        }

        techRepo.deleteById(id);
        return String.format("Technician '%s %s' successfully migrated to %s",
                t.getFirstName(), t.getLastName(), to);
    }

    // ── FROM STAFF ────────────────────────────────────────────────────────────

    private String migrateFromStaff(Long id, String to) {
        StaffOperator s = staffRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff operator not found with id: " + id));

        checkUsernameConflict(s.getUsername(), "staff");
        checkEmailConflict(s.getEmail(), "staff");
        checkEmployeeIdConflict(s.getEmployeeId(), "staff");

        switch (to) {
            case "technician" -> {
                Technician t = new Technician();
                copyCommon(t, s.getFirstName(), s.getLastName(), s.getEmail(),
                        s.getPassword(), s.getPhone(), s.getUsername(),
                        s.getEmployeeId(), s.isActive());
                t.setSpecialization(s.getJobTitle());
                t.setExperienceYears(s.getExperienceYears());
                t.setCertifications(s.getCertifications());
                t.setNotes(s.getNotes());
                techRepo.save(t);
            }
            case "manager" -> {
                Manager m = new Manager();
                copyCommon(m, s.getFirstName(), s.getLastName(), s.getEmail(),
                        s.getPassword(), s.getPhone(), s.getUsername(),
                        s.getEmployeeId(), s.isActive());
                m.setJobTitle(s.getJobTitle());
                m.setExperienceYears(s.getExperienceYears());
                m.setCertifications(s.getCertifications());
                m.setNotes(s.getNotes());
                if (s.getDepartment() != null) {
                    try {
                        m.setManagedDepartment(
                                Manager.Department.valueOf(s.getDepartment().name()));
                    } catch (IllegalArgumentException ignored) {}
                }
                managerRepo.save(m);
            }
            case "admin" -> {
                Admin a = new Admin();
                copyCommon(a, s.getFirstName(), s.getLastName(), s.getEmail(),
                        s.getPassword(), s.getPhone(), s.getUsername(),
                        s.getEmployeeId(), s.isActive());
                if (s.getDepartment() != null)
                    a.setDepartment(s.getDepartment().name());
                adminRepo.save(a);
            }
            default -> throw new IllegalArgumentException("Unknown toRole: " + to);
        }

        staffRepo.deleteById(id);
        return String.format("Staff operator '%s %s' successfully migrated to %s",
                s.getFirstName(), s.getLastName(), to);
    }

    // ── FROM MANAGER ──────────────────────────────────────────────────────────

    private String migrateFromManager(Long id, String to) {
        Manager m = managerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Manager not found with id: " + id));

        checkUsernameConflict(m.getUsername(), "manager");
        checkEmailConflict(m.getEmail(), "manager");
        checkEmployeeIdConflict(m.getEmployeeId(), "manager");

        switch (to) {
            case "technician" -> {
                Technician t = new Technician();
                copyCommon(t, m.getFirstName(), m.getLastName(), m.getEmail(),
                        m.getPassword(), m.getPhone(), m.getUsername(),
                        m.getEmployeeId(), m.isActive());
                t.setSpecialization(m.getJobTitle());
                t.setExperienceYears(m.getExperienceYears());
                t.setCertifications(m.getCertifications());
                t.setNotes(m.getNotes());
                techRepo.save(t);
            }
            case "staff" -> {
                StaffOperator s = new StaffOperator();
                copyCommon(s, m.getFirstName(), m.getLastName(), m.getEmail(),
                        m.getPassword(), m.getPhone(), m.getUsername(),
                        m.getEmployeeId(), m.isActive());
                s.setJobTitle(m.getJobTitle());
                s.setExperienceYears(m.getExperienceYears());
                s.setCertifications(m.getCertifications());
                s.setNotes(m.getNotes());
                if (m.getManagedDepartment() != null) {
                    try {
                        s.setDepartment(StaffOperator.Department.valueOf(
                                m.getManagedDepartment().name()));
                    } catch (IllegalArgumentException ignored) {}
                }
                staffRepo.save(s);
            }
            case "admin" -> {
                Admin a = new Admin();
                copyCommon(a, m.getFirstName(), m.getLastName(), m.getEmail(),
                        m.getPassword(), m.getPhone(), m.getUsername(),
                        m.getEmployeeId(), m.isActive());
                if (m.getManagedDepartment() != null)
                    a.setDepartment(m.getManagedDepartment().name());
                adminRepo.save(a);
            }
            default -> throw new IllegalArgumentException("Unknown toRole: " + to);
        }

        managerRepo.deleteById(id);
        return String.format("Manager '%s %s' successfully migrated to %s",
                m.getFirstName(), m.getLastName(), to);
    }

    // ── FROM ADMIN ────────────────────────────────────────────────────────────

    private String migrateFromAdmin(Long id, String to) {
        Admin a = adminRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found with id: " + id));

        checkUsernameConflict(a.getUsername(), "admin");
        checkEmailConflict(a.getEmail(), "admin");
        checkEmployeeIdConflict(a.getEmployeeId(), "admin");

        switch (to) {
            case "technician" -> {
                Technician t = new Technician();
                copyCommon(t, a.getFirstName(), a.getLastName(), a.getEmail(),
                        a.getPassword(), a.getPhone(), a.getUsername(),
                        a.getEmployeeId(), a.isActive());
                techRepo.save(t);
            }
            case "staff" -> {
                StaffOperator s = new StaffOperator();
                copyCommon(s, a.getFirstName(), a.getLastName(), a.getEmail(),
                        a.getPassword(), a.getPhone(), a.getUsername(),
                        a.getEmployeeId(), a.isActive());
                if (a.getDepartment() != null) {
                    try {
                        s.setDepartment(StaffOperator.Department.valueOf(
                                a.getDepartment().toUpperCase()));
                    } catch (IllegalArgumentException ignored) {}
                }
                staffRepo.save(s);
            }
            case "manager" -> {
                Manager m = new Manager();
                copyCommon(m, a.getFirstName(), a.getLastName(), a.getEmail(),
                        a.getPassword(), a.getPhone(), a.getUsername(),
                        a.getEmployeeId(), a.isActive());
                if (a.getDepartment() != null) {
                    try {
                        m.setManagedDepartment(Manager.Department.valueOf(
                                a.getDepartment().toUpperCase()));
                    } catch (IllegalArgumentException ignored) {}
                }
                managerRepo.save(m);
            }
            default -> throw new IllegalArgumentException("Unknown toRole: " + to);
        }

        adminRepo.deleteById(id);
        return String.format("Admin '%s %s' successfully migrated to %s",
                a.getFirstName(), a.getLastName(), to);
    }

    // ── Generic copyCommon helpers ────────────────────────────────────────────
    //
    // KEY FIX: employeeId is only set when non-null and non-blank.
    // This prevents duplicate unique-key violations because MySQL unique
    // indexes reject multiple NULLs only in certain configurations, and
    // a technician/staff record with an employeeId that already exists in
    // the target table will now be caught by checkEmployeeIdConflict()
    // BEFORE we attempt the INSERT.

    private void copyCommon(Technician t,
                            String first, String last, String email,
                            String pwHash, String phone, String username,
                            String empId, boolean active) {
        t.setFirstName(first);
        t.setLastName(last);
        t.setEmail(email);
        t.setPassword(pwHash);
        t.setPhone(phone);
        t.setUsername(username);
        t.setActive(active);
        if (empId != null && !empId.isBlank()) t.setEmployeeId(empId);
    }

    private void copyCommon(StaffOperator s,
                            String first, String last, String email,
                            String pwHash, String phone, String username,
                            String empId, boolean active) {
        s.setFirstName(first);
        s.setLastName(last);
        s.setEmail(email);
        s.setPassword(pwHash);
        s.setPhone(phone);
        s.setUsername(username);
        s.setActive(active);
        if (empId != null && !empId.isBlank()) s.setEmployeeId(empId);
    }

    private void copyCommon(Manager m,
                            String first, String last, String email,
                            String pwHash, String phone, String username,
                            String empId, boolean active) {
        m.setFirstName(first);
        m.setLastName(last);
        m.setEmail(email);
        m.setPassword(pwHash);
        m.setPhone(phone);
        m.setUsername(username);
        m.setActive(active);
        if (empId != null && !empId.isBlank()) m.setEmployeeId(empId);
    }

    private void copyCommon(Admin a,
                            String first, String last, String email,
                            String pwHash, String phone, String username,
                            String empId, boolean active) {
        a.setFirstName(first);
        a.setLastName(last);
        a.setEmail(email);
        a.setPassword(pwHash);
        a.setPhone(phone);
        a.setUsername(username);
        a.setActive(active);
        if (empId != null && !empId.isBlank()) a.setEmployeeId(empId);
    }

    // ── Conflict checks ───────────────────────────────────────────────────────

    /**
     * Verify the username does not already exist in any table OTHER than
     * the source table (the source row is about to be deleted, so it won't
     * be a conflict).
     */
    private void checkUsernameConflict(String username, String skipTable) {
        if (username == null || username.isBlank()) return;
        if (!skipTable.equals("admin") && adminRepo.existsByUsername(username))
            throw new IllegalArgumentException(
                    "Username '" + username + "' already exists in the admin table.");
        if (!skipTable.equals("technician") && techRepo.existsByUsername(username))
            throw new IllegalArgumentException(
                    "Username '" + username + "' already exists in the technician table.");
        if (!skipTable.equals("staff") && staffRepo.existsByUsername(username))
            throw new IllegalArgumentException(
                    "Username '" + username + "' already exists in the staff table.");
        if (!skipTable.equals("manager") && managerRepo.existsByUsername(username))
            throw new IllegalArgumentException(
                    "Username '" + username + "' already exists in the manager table.");
    }

    private void checkEmailConflict(String email, String skipTable) {
        if (email == null || email.isBlank()) return;
        if (!skipTable.equals("admin") && adminRepo.existsByEmail(email))
            throw new IllegalArgumentException(
                    "Email '" + email + "' already exists in the admin table.");
        if (!skipTable.equals("technician") && techRepo.existsByEmail(email))
            throw new IllegalArgumentException(
                    "Email '" + email + "' already exists in the technician table.");
        if (!skipTable.equals("staff") && staffRepo.existsByEmail(email))
            throw new IllegalArgumentException(
                    "Email '" + email + "' already exists in the staff table.");
        if (!skipTable.equals("manager") && managerRepo.existsByEmail(email))
            throw new IllegalArgumentException(
                    "Email '" + email + "' already exists in the manager table.");
    }

    /**
     * KEY FIX: Checks that the employeeId doesn't collide in any OTHER table.
     * Skips the check when employeeId is null/blank (nulls are allowed to
     * repeat in a nullable unique column under MySQL 8).
     */
    private void checkEmployeeIdConflict(String employeeId, String skipTable) {
        if (employeeId == null || employeeId.isBlank()) return;
        if (!skipTable.equals("admin") && adminRepo.existsByEmployeeId(employeeId))
            throw new IllegalArgumentException(
                    "Employee ID '" + employeeId + "' already exists in the admin table.");
        if (!skipTable.equals("technician") && techRepo.existsByEmployeeId(employeeId))
            throw new IllegalArgumentException(
                    "Employee ID '" + employeeId + "' already exists in the technician table.");
        if (!skipTable.equals("staff") && staffRepo.existsByEmployeeId(employeeId))
            throw new IllegalArgumentException(
                    "Employee ID '" + employeeId + "' already exists in the staff table.");
        if (!skipTable.equals("manager") && managerRepo.existsByEmployeeId(employeeId))
            throw new IllegalArgumentException(
                    "Employee ID '" + employeeId + "' already exists in the manager table.");
    }
}