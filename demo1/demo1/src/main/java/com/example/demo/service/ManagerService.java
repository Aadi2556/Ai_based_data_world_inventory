package com.example.demo.service;

import com.example.demo.config.JwtUtil;
import com.example.demo.dto.*;
import com.example.demo.model.Manager;
import com.example.demo.model.Manager.Department;
import com.example.demo.repository.ManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service("managerUserDetailsService")
public class ManagerService implements UserDetailsService {

    @Autowired private ManagerRepository managerRepo;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private TwoFactorService twoFactorService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Manager manager = managerRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Manager not found: " + username));
        return new User(
                manager.getUsername(),
                manager.getPassword(),
                manager.isActive(),
                true, true, true,
                List.of(new SimpleGrantedAuthority("ROLE_MANAGER"))
        );
    }

    // ── Auth ──
    @Transactional
    public ManagerAuthResponse register(ManagerRegisterRequest req) {
        if (managerRepo.existsByEmail(req.getEmail()))
            throw new RuntimeException("Email already exists");
        if (managerRepo.existsByUsername(req.getUsername()))
            throw new RuntimeException("Username already taken");
        if (req.getEmployeeId() != null && !req.getEmployeeId().isBlank()
                && managerRepo.existsByEmployeeId(req.getEmployeeId()))
            throw new RuntimeException("Employee ID already exists");

        Manager m = new Manager();
        m.setFirstName(req.getFirstName());
        m.setLastName(req.getLastName());
        m.setEmail(req.getEmail());
        m.setPassword(passwordEncoder.encode(req.getPassword()));
        m.setPhone(req.getPhone());
        m.setUsername(req.getUsername());
        m.setEmployeeId(req.getEmployeeId());
        m.setJobTitle(req.getJobTitle());
        m.setExperienceYears(req.getExperienceYears());
        m.setCertifications(req.getCertifications());
        m.setNotes(req.getNotes());
        m.setActive(true);

        if (req.getManagedDepartment() != null && !req.getManagedDepartment().isBlank()) {
            try {
                m.setManagedDepartment(Department.valueOf(req.getManagedDepartment().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid department: " + req.getManagedDepartment());
            }
        }

        Manager saved = managerRepo.save(m);
        String token = jwtUtil.generateToken("MANAGER:" + saved.getUsername());
        return new ManagerAuthResponse(token, toProfile(saved), "Registration successful");
    }

    public ManagerAuthResponse login(LoginRequest req) {
        Manager m = managerRepo.findByUsername(req.getUsername())
                .or(() -> managerRepo.findByEmail(req.getUsername()))
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));
        if (!passwordEncoder.matches(req.getPassword(), m.getPassword()))
            throw new RuntimeException("Invalid username or password");
        if (!m.isActive())
            throw new RuntimeException("Account is deactivated");

        if (req.getOtp() == null || req.getOtp().isEmpty()) {
            twoFactorService.generateAndSendOtp(m.getUsername(), "MANAGER", m.getEmail());
            return new ManagerAuthResponse(null, null, "OTP sent to your email");
        } else {
            boolean isOtpValid = twoFactorService.verifyOtp(m.getUsername(), "MANAGER", req.getOtp());
            if (!isOtpValid) {
                throw new RuntimeException("Invalid or expired OTP");
            }
            String token = jwtUtil.generateToken("MANAGER:" + m.getUsername());
            return new ManagerAuthResponse(token, toProfile(m), "Login successful");
        }
    }

    // ── Self profile ──
    public ManagerProfileResponse getProfile(String username) {
        Manager m = managerRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Manager not found"));
        return toProfile(m);
    }

    @Transactional
    public ManagerProfileResponse updateProfile(String username, ManagerUpdateRequest req) {
        Manager m = managerRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Manager not found"));
        applyUpdate(m, req);
        return toProfile(managerRepo.save(m));
    }

    @Transactional
    public void deleteProfile(String username) {
        Manager m = managerRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Manager not found"));
        managerRepo.delete(m);
    }

    // ── Admin operations ──
    public List<ManagerProfileResponse> getAllManagers() {
        return managerRepo.findAllByOrderByCreatedAtDesc()
                .stream().map(this::toProfile).collect(Collectors.toList());
    }

    public List<ManagerProfileResponse> getActiveManagers() {
        return managerRepo.findByIsActiveTrueOrderByFirstNameAsc()
                .stream().map(this::toProfile).collect(Collectors.toList());
    }

    public ManagerProfileResponse getManagerById(Long id) {
        return toProfile(managerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Manager not found")));
    }

    @Transactional
    public ManagerProfileResponse adminCreateManager(ManagerRegisterRequest req) {
        return register(req).getManager();
    }

    @Transactional
    public ManagerProfileResponse adminUpdateManager(Long id, ManagerUpdateRequest req) {
        Manager m = managerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Manager not found"));
        applyUpdate(m, req);
        return toProfile(managerRepo.save(m));
    }

    @Transactional
    public ManagerProfileResponse deactivateManager(Long id) {
        Manager m = managerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Manager not found"));
        m.setActive(false);
        return toProfile(managerRepo.save(m));
    }

    @Transactional
    public ManagerProfileResponse reactivateManager(Long id) {
        Manager m = managerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Manager not found"));
        m.setActive(true);
        return toProfile(managerRepo.save(m));
    }

    @Transactional
    public void deleteManager(Long id) {
        if (!managerRepo.existsById(id)) throw new RuntimeException("Manager not found");
        managerRepo.deleteById(id);
    }

    // ── Helpers ──
    private void applyUpdate(Manager m, ManagerUpdateRequest req) {
        if (req.getFirstName() != null) m.setFirstName(req.getFirstName());
        if (req.getLastName() != null) m.setLastName(req.getLastName());
        if (req.getPhone() != null) m.setPhone(req.getPhone());
        if (req.getJobTitle() != null) m.setJobTitle(req.getJobTitle());
        if (req.getExperienceYears() != null) m.setExperienceYears(req.getExperienceYears());
        if (req.getCertifications() != null) m.setCertifications(req.getCertifications());
        if (req.getNotes() != null) m.setNotes(req.getNotes());

        if (req.getManagedDepartment() != null && !req.getManagedDepartment().isBlank()) {
            try {
                m.setManagedDepartment(Department.valueOf(req.getManagedDepartment().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid department: " + req.getManagedDepartment());
            }
        }

        if (req.getEmail() != null && !req.getEmail().equals(m.getEmail())) {
            if (managerRepo.existsByEmail(req.getEmail()))
                throw new RuntimeException("Email already in use");
            m.setEmail(req.getEmail());
        }

        if (req.getNewPassword() != null && !req.getNewPassword().isBlank()) {
            if (req.getCurrentPassword() == null
                    || !passwordEncoder.matches(req.getCurrentPassword(), m.getPassword()))
                throw new RuntimeException("Current password is incorrect");
            m.setPassword(passwordEncoder.encode(req.getNewPassword()));
        }
    }

    private ManagerProfileResponse toProfile(Manager m) {
        ManagerProfileResponse r = new ManagerProfileResponse();
        r.setId(m.getId());
        r.setFirstName(m.getFirstName());
        r.setLastName(m.getLastName());
        r.setEmail(m.getEmail());
        r.setPhone(m.getPhone());
        r.setUsername(m.getUsername());
        r.setEmployeeId(m.getEmployeeId());
        r.setManagedDepartment(m.getManagedDepartment() != null ? m.getManagedDepartment().name() : null);
        r.setJobTitle(m.getJobTitle());
        r.setExperienceYears(m.getExperienceYears());
        r.setCertifications(m.getCertifications());
        r.setProfileImage(m.getProfileImage());
        r.setNotes(m.getNotes());
        r.setActive(m.isActive());
        r.setCreatedAt(m.getCreatedAt() != null ? m.getCreatedAt().toString() : null);
        r.setUpdatedAt(m.getUpdatedAt() != null ? m.getUpdatedAt().toString() : null);
        return r;
    }
}