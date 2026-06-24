package com.example.demo.service;

import com.example.demo.config.JwtUtil;
import com.example.demo.dto.*;
import com.example.demo.model.MaintenanceSchedule;
import com.example.demo.model.RepairLog;
import com.example.demo.model.Technician;
import com.example.demo.model.Technician.AvailabilityStatus;
import com.example.demo.repository.MaintenanceScheduleRepository;
import com.example.demo.repository.RepairLogRepository;
import com.example.demo.repository.TechnicianRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service("technicianUserDetailsService")
public class TechnicianService implements UserDetailsService {

    @Autowired private TechnicianRepository          techRepo;
    @Autowired private MaintenanceScheduleRepository scheduleRepo;
    @Autowired private RepairLogRepository           repairRepo;
    @Autowired private PasswordEncoder               passwordEncoder;
    @Autowired private JwtUtil                       jwtUtil;
    @Autowired private TwoFactorService              twoFactorService;

    // ── UserDetailsService ───────────────────────────────────────────────────

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        Technician tech = techRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Technician not found: " + username));
        return new User(
                tech.getUsername(),
                tech.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_TECHNICIAN"))
        );
    }

    // ── Auth ─────────────────────────────────────────────────────────────────

    public TechnicianAuthResponse register(TechnicianRegisterRequest req) {
        if (techRepo.existsByEmail(req.getEmail()))
            throw new RuntimeException("Email already exists");
        if (techRepo.existsByUsername(req.getUsername()))
            throw new RuntimeException("Username already taken");
        if (req.getEmployeeId() != null && !req.getEmployeeId().isBlank()
                && techRepo.existsByEmployeeId(req.getEmployeeId()))
            throw new RuntimeException("Employee ID already exists");

        Technician t = new Technician();
        t.setFirstName(req.getFirstName());
        t.setLastName(req.getLastName());
        t.setEmail(req.getEmail());
        t.setPassword(passwordEncoder.encode(req.getPassword()));
        t.setPhone(req.getPhone());
        t.setUsername(req.getUsername());
        t.setEmployeeId(req.getEmployeeId());
        t.setSpecialization(req.getSpecialization());
        t.setExperienceYears(req.getExperienceYears());
        t.setCertifications(req.getCertifications());
        t.setNotes(req.getNotes());
        t.setActive(true);

        Technician saved = techRepo.save(t);
        String token = jwtUtil.generateToken("TECH:" + saved.getUsername());
        return new TechnicianAuthResponse(token, toProfile(saved),
                "Registration successful");
    }

    public TechnicianAuthResponse login(LoginRequest req) {
        Technician t = techRepo.findByUsername(req.getUsername())
                .or(() -> techRepo.findByEmail(req.getUsername()))
                .orElseThrow(() -> new RuntimeException(
                        "Invalid username or password"));
        if (!passwordEncoder.matches(req.getPassword(), t.getPassword()))
            throw new RuntimeException("Invalid username or password");
        if (!t.isActive())
            throw new RuntimeException("Account is deactivated");

        if (req.getOtp() == null || req.getOtp().isEmpty()) {
            twoFactorService.generateAndSendOtp(t.getUsername(), "TECHNICIAN", t.getEmail());
            return new TechnicianAuthResponse(null, null, "OTP sent to your email");
        } else {
            boolean isOtpValid = twoFactorService.verifyOtp(t.getUsername(), "TECHNICIAN", req.getOtp());
            if (!isOtpValid) {
                throw new RuntimeException("Invalid or expired OTP");
            }
            String token = jwtUtil.generateToken("TECH:" + t.getUsername());
            return new TechnicianAuthResponse(token, toProfile(t), "Login successful");
        }
    }

    // ── Self-service profile (NO delete — technicians cannot delete themselves) ──

    public TechnicianProfileResponse getProfile(String username) {
        Technician t = techRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Technician not found"));
        return toProfile(t);
    }

    public TechnicianProfileResponse updateProfile(String username,
                                                   TechnicianUpdateRequest req) {
        Technician t = techRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Technician not found"));
        applyUpdate(t, req);
        return toProfile(techRepo.save(t));
    }

    // NOTE: deleteProfile(String username) has been removed.
    // Technicians cannot delete their own accounts.
    // Deletion is handled by admins/managers via deleteTechnician(Long id) below.

    // ── Admin operations ──────────────────────────────────────────────────────

    public List<TechnicianProfileResponse> getAllTechnicians() {
        return techRepo.findAllByOrderByCreatedAtDesc()
                .stream().map(this::toProfileWithWorkload)
                .collect(Collectors.toList());
    }

    public List<TechnicianProfileResponse> getActiveTechnicians() {
        return techRepo.findByIsActiveTrueOrderByFirstNameAsc()
                .stream().map(this::toProfileWithWorkload)
                .collect(Collectors.toList());
    }

    public TechnicianProfileResponse getTechnicianById(Long id) {
        return toProfileWithWorkload(techRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Technician not found")));
    }

    public TechnicianProfileResponse adminCreateTechnician(
            TechnicianRegisterRequest req) {
        return register(req).getTechnician();
    }

    public TechnicianProfileResponse adminUpdateTechnician(Long id,
                                                           TechnicianUpdateRequest req) {
        Technician t = techRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Technician not found"));
        applyUpdate(t, req);
        return toProfileWithWorkload(techRepo.save(t));
    }

    public TechnicianProfileResponse removeFromTask(Long id) {
        Technician t = techRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Technician not found"));
        scheduleRepo.findAllByOrderByScheduledDateDesc().stream()
                .filter(s -> s.getStatus() == MaintenanceSchedule.Status.SCHEDULED
                        || s.getStatus() == MaintenanceSchedule.Status.IN_PROGRESS)
                .filter(s -> t.getUsername().equals(s.getAssignedTechnician())
                        || (t.getFirstName() + " " + t.getLastName())
                        .equals(s.getAssignedTechnician()))
                .forEach(s -> { s.setAssignedTechnician(null); scheduleRepo.save(s); });

        repairRepo.findAllByOrderByReportedDateDesc().stream()
                .filter(r -> r.getStatus() == RepairLog.RepairStatus.OPEN
                        || r.getStatus() == RepairLog.RepairStatus.IN_PROGRESS)
                .filter(r -> t.getUsername().equals(r.getTechnician())
                        || (t.getFirstName() + " " + t.getLastName())
                        .equals(r.getTechnician()))
                .forEach(r -> { r.setTechnician(null); repairRepo.save(r); });

        t.setAvailabilityStatus(AvailabilityStatus.AVAILABLE);
        return toProfileWithWorkload(techRepo.save(t));
    }

    public TechnicianProfileResponse deactivateTechnician(Long id) {
        Technician t = techRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Technician not found"));
        t.setActive(false);
        t.setAvailabilityStatus(AvailabilityStatus.INACTIVE);
        return toProfile(techRepo.save(t));
    }

    public TechnicianProfileResponse reactivateTechnician(Long id) {
        Technician t = techRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Technician not found"));
        t.setActive(true);
        t.setAvailabilityStatus(AvailabilityStatus.AVAILABLE);
        return toProfileWithWorkload(techRepo.save(t));
    }

    /** Hard delete — admin/manager only (called from AdminTechnicianController). */
    public void deleteTechnician(Long id) {
        if (!techRepo.existsById(id))
            throw new RuntimeException("Technician not found");
        techRepo.deleteById(id);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void applyUpdate(Technician t, TechnicianUpdateRequest req) {
        if (req.getFirstName() != null)       t.setFirstName(req.getFirstName());
        if (req.getLastName() != null)        t.setLastName(req.getLastName());
        if (req.getPhone() != null)           t.setPhone(req.getPhone());
        if (req.getSpecialization() != null)  t.setSpecialization(req.getSpecialization());
        if (req.getExperienceYears() != null) t.setExperienceYears(req.getExperienceYears());
        if (req.getCertifications() != null)  t.setCertifications(req.getCertifications());
        if (req.getNotes() != null)           t.setNotes(req.getNotes());
        if (req.getAvailabilityStatus() != null)
            t.setAvailabilityStatus(
                    AvailabilityStatus.valueOf(req.getAvailabilityStatus()));

        if (req.getEmail() != null && !req.getEmail().equals(t.getEmail())) {
            if (techRepo.existsByEmail(req.getEmail()))
                throw new RuntimeException("Email already in use");
            t.setEmail(req.getEmail());
        }
        if (req.getNewPassword() != null && !req.getNewPassword().isBlank()) {
            if (req.getCurrentPassword() == null
                    || !passwordEncoder.matches(req.getCurrentPassword(), t.getPassword()))
                throw new RuntimeException("Current password is incorrect");
            t.setPassword(passwordEncoder.encode(req.getNewPassword()));
        }
    }

    private TechnicianProfileResponse toProfile(Technician t) {
        TechnicianProfileResponse r = new TechnicianProfileResponse();
        r.setId(t.getId());
        r.setFirstName(t.getFirstName());
        r.setLastName(t.getLastName());
        r.setEmail(t.getEmail());
        r.setPhone(t.getPhone());
        r.setUsername(t.getUsername());
        r.setEmployeeId(t.getEmployeeId());
        r.setSpecialization(t.getSpecialization());
        r.setExperienceYears(t.getExperienceYears());
        r.setCertifications(t.getCertifications());
        r.setAvailabilityStatus(t.getAvailabilityStatus().name());
        r.setProfileImage(t.getProfileImage());
        r.setNotes(t.getNotes());
        r.setActive(t.isActive());
        r.setCreatedAt(t.getCreatedAt() != null ? t.getCreatedAt().toString() : null);
        r.setUpdatedAt(t.getUpdatedAt() != null ? t.getUpdatedAt().toString() : null);
        return r;
    }

    private TechnicianProfileResponse toProfileWithWorkload(Technician t) {
        TechnicianProfileResponse r = toProfile(t);
        String nameMatch     = t.getFirstName() + " " + t.getLastName();
        String usernameMatch = t.getUsername();

        List<MaintenanceSchedule> assignedSchedules = scheduleRepo.findAll().stream()
                .filter(s -> usernameMatch.equals(s.getAssignedTechnician()) || nameMatch.equals(s.getAssignedTechnician()))
                .collect(Collectors.toList());
                
        r.setTotalAssignedSchedules((int) assignedSchedules.stream()
                .filter(s -> s.getStatus() == MaintenanceSchedule.Status.SCHEDULED || s.getStatus() == MaintenanceSchedule.Status.IN_PROGRESS)
                .count());
                
        r.setCompletedSchedules((int) assignedSchedules.stream()
                .filter(s -> s.getStatus() == MaintenanceSchedule.Status.COMPLETED)
                .count());

        List<RepairLog> assignedRepairs = repairRepo.findAll().stream()
                .filter(rep -> usernameMatch.equals(rep.getTechnician()) || nameMatch.equals(rep.getTechnician()))
                .collect(Collectors.toList());
                
        r.setOpenRepairs((int) assignedRepairs.stream()
                .filter(rep -> rep.getStatus() == RepairLog.RepairStatus.OPEN || rep.getStatus() == RepairLog.RepairStatus.IN_PROGRESS)
                .count());
                
        r.setResolvedRepairs((int) assignedRepairs.stream()
                .filter(rep -> rep.getStatus() == RepairLog.RepairStatus.RESOLVED)
                .count());

        return r;
    }
}
