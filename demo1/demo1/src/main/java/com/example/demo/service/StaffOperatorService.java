package com.example.demo.service;

import com.example.demo.config.JwtUtil;
import com.example.demo.dto.*;
import com.example.demo.model.StaffOperator;
import com.example.demo.model.StaffOperator.AvailabilityStatus;
import com.example.demo.model.StaffOperator.Department;
import com.example.demo.repository.StaffOperatorRepository;
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

@Service("staffOperatorUserDetailsService")
public class StaffOperatorService implements UserDetailsService {

    @Autowired private StaffOperatorRepository staffRepo;
    @Autowired private PasswordEncoder         passwordEncoder;
    @Autowired private JwtUtil                 jwtUtil;
    @Autowired private TwoFactorService        twoFactorService;

    // ── UserDetailsService ───────────────────────────────────────────────────

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        StaffOperator staff = staffRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Staff operator not found: " + username));
        return new User(
                staff.getUsername(),
                staff.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_STAFF"))
        );
    }

    // ── Auth ──────────────────────────────────────────────────────────────────

    public StaffOperatorAuthResponse register(StaffOperatorRegisterRequest req) {
        if (staffRepo.existsByEmail(req.getEmail()))
            throw new RuntimeException("Email already exists");
        if (staffRepo.existsByUsername(req.getUsername()))
            throw new RuntimeException("Username already taken");
        if (req.getEmployeeId() != null && !req.getEmployeeId().isBlank()
                && staffRepo.existsByEmployeeId(req.getEmployeeId()))
            throw new RuntimeException("Employee ID already exists");

        StaffOperator s = new StaffOperator();
        s.setFirstName(req.getFirstName());
        s.setLastName(req.getLastName());
        s.setEmail(req.getEmail());
        s.setPassword(passwordEncoder.encode(req.getPassword()));
        s.setPhone(req.getPhone());
        s.setUsername(req.getUsername());
        s.setEmployeeId(req.getEmployeeId());
        s.setJobTitle(req.getJobTitle());
        s.setShift(req.getShift());
        s.setExperienceYears(req.getExperienceYears());
        s.setCertifications(req.getCertifications());
        s.setNotes(req.getNotes());
        s.setActive(true);

        if (req.getDepartment() != null && !req.getDepartment().isBlank()) {
            try {
                s.setDepartment(Department.valueOf(req.getDepartment().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid department: " + req.getDepartment());
            }
        }

        StaffOperator saved = staffRepo.save(s);
        String token = jwtUtil.generateToken("STAFF:" + saved.getUsername());
        return new StaffOperatorAuthResponse(token, toProfile(saved),
                "Registration successful");
    }

    public StaffOperatorAuthResponse login(LoginRequest req) {
        StaffOperator s = staffRepo.findByUsername(req.getUsername())
                .or(() -> staffRepo.findByEmail(req.getUsername()))
                .orElseThrow(() -> new RuntimeException(
                        "Invalid username or password"));
        if (!passwordEncoder.matches(req.getPassword(), s.getPassword()))
            throw new RuntimeException("Invalid username or password");
        if (!s.isActive())
            throw new RuntimeException("Account is deactivated");

        if (req.getOtp() == null || req.getOtp().isEmpty()) {
            twoFactorService.generateAndSendOtp(s.getUsername(), "STAFF", s.getEmail());
            return new StaffOperatorAuthResponse(null, null, "OTP sent to your email");
        } else {
            boolean isOtpValid = twoFactorService.verifyOtp(s.getUsername(), "STAFF", req.getOtp());
            if (!isOtpValid) {
                throw new RuntimeException("Invalid or expired OTP");
            }
            String token = jwtUtil.generateToken("STAFF:" + s.getUsername());
            return new StaffOperatorAuthResponse(token, toProfile(s), "Login successful");
        }
    }

    // ── Self-service profile (NO delete — staff cannot delete themselves) ─────

    public StaffOperatorProfileResponse getProfile(String username) {
        StaffOperator s = staffRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(
                        "Staff operator not found"));
        return toProfile(s);
    }

    public StaffOperatorProfileResponse updateProfile(String username,
                                                      StaffOperatorUpdateRequest req) {
        StaffOperator s = staffRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(
                        "Staff operator not found"));
        applyUpdate(s, req);
        return toProfile(staffRepo.save(s));
    }

    // NOTE: deleteProfile(String username) has been removed.
    // Staff cannot delete their own accounts.
    // Deletion is handled by admins/managers via deleteStaff(Long id) below.

    // ── Admin operations ──────────────────────────────────────────────────────

    public List<StaffOperatorProfileResponse> getAllStaff() {
        return staffRepo.findAllByOrderByCreatedAtDesc()
                .stream().map(this::toProfile).collect(Collectors.toList());
    }

    public List<StaffOperatorProfileResponse> getActiveStaff() {
        return staffRepo.findByIsActiveTrueOrderByFirstNameAsc()
                .stream().map(this::toProfile).collect(Collectors.toList());
    }

    public StaffOperatorProfileResponse getStaffById(Long id) {
        return toProfile(staffRepo.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Staff operator not found")));
    }

    public StaffOperatorProfileResponse adminCreateStaff(
            StaffOperatorRegisterRequest req) {
        return register(req).getStaff();
    }

    public StaffOperatorProfileResponse adminUpdateStaff(Long id,
                                                         StaffOperatorUpdateRequest req) {
        StaffOperator s = staffRepo.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Staff operator not found"));
        applyUpdate(s, req);
        return toProfile(staffRepo.save(s));
    }

    public StaffOperatorProfileResponse deactivateStaff(Long id) {
        StaffOperator s = staffRepo.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Staff operator not found"));
        s.setActive(false);
        s.setAvailabilityStatus(AvailabilityStatus.INACTIVE);
        return toProfile(staffRepo.save(s));
    }

    /** Reactivate staff account. */
    public StaffOperatorProfileResponse reactivateStaff(Long id) {
        StaffOperator s = staffRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff operator not found"));
        s.setActive(true);
        if (s.getAvailabilityStatus() == AvailabilityStatus.INACTIVE) {
            s.setAvailabilityStatus(AvailabilityStatus.AVAILABLE);
        }
        return toProfile(staffRepo.save(s));
    }

    /** Hard delete — admin/manager only (called from AdminStaffController). */
    public void deleteStaff(Long id) {
        if (!staffRepo.existsById(id))
            throw new RuntimeException("Staff operator not found");
        staffRepo.deleteById(id);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void applyUpdate(StaffOperator s, StaffOperatorUpdateRequest req) {
        if (req.getFirstName() != null)       s.setFirstName(req.getFirstName());
        if (req.getLastName() != null)        s.setLastName(req.getLastName());
        if (req.getPhone() != null)           s.setPhone(req.getPhone());
        if (req.getJobTitle() != null)        s.setJobTitle(req.getJobTitle());
        if (req.getShift() != null)           s.setShift(req.getShift());
        if (req.getExperienceYears() != null) s.setExperienceYears(req.getExperienceYears());
        if (req.getCertifications() != null)  s.setCertifications(req.getCertifications());
        if (req.getNotes() != null)           s.setNotes(req.getNotes());

        if (req.getDepartment() != null && !req.getDepartment().isBlank()) {
            try {
                s.setDepartment(
                        Department.valueOf(req.getDepartment().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(
                        "Invalid department: " + req.getDepartment());
            }
        }

        if (req.getAvailabilityStatus() != null
                && !req.getAvailabilityStatus().isBlank()) {
            try {
                s.setAvailabilityStatus(AvailabilityStatus.valueOf(
                        req.getAvailabilityStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(
                        "Invalid availability status: " + req.getAvailabilityStatus());
            }
        }

        if (req.getEmail() != null && !req.getEmail().equals(s.getEmail())) {
            if (staffRepo.existsByEmail(req.getEmail()))
                throw new RuntimeException("Email already in use");
            s.setEmail(req.getEmail());
        }

        if (req.getNewPassword() != null && !req.getNewPassword().isBlank()) {
            if (req.getCurrentPassword() == null
                    || !passwordEncoder.matches(req.getCurrentPassword(),
                    s.getPassword()))
                throw new RuntimeException("Current password is incorrect");
            s.setPassword(passwordEncoder.encode(req.getNewPassword()));
        }
    }

    private StaffOperatorProfileResponse toProfile(StaffOperator s) {
        StaffOperatorProfileResponse r = new StaffOperatorProfileResponse();
        r.setId(s.getId());
        r.setFirstName(s.getFirstName());
        r.setLastName(s.getLastName());
        r.setEmail(s.getEmail());
        r.setPhone(s.getPhone());
        r.setUsername(s.getUsername());
        r.setEmployeeId(s.getEmployeeId());
        r.setDepartment(s.getDepartment() != null ? s.getDepartment().name() : null);
        r.setJobTitle(s.getJobTitle());
        r.setShift(s.getShift());
        r.setExperienceYears(s.getExperienceYears());
        r.setCertifications(s.getCertifications());
        r.setAvailabilityStatus(s.getAvailabilityStatus().name());
        r.setProfileImage(s.getProfileImage());
        r.setNotes(s.getNotes());
        r.setActive(s.isActive());
        r.setCreatedAt(s.getCreatedAt() != null ? s.getCreatedAt().toString() : null);
        r.setUpdatedAt(s.getUpdatedAt() != null ? s.getUpdatedAt().toString() : null);
        return r;
    }
}