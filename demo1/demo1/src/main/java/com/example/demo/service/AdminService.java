package com.example.demo.service;

import com.example.demo.config.JwtUtil;
import com.example.demo.dto.*;
import com.example.demo.model.Admin;
import com.example.demo.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService implements UserDetailsService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TwoFactorService twoFactorService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found: " + username));
        return new User(
                admin.getUsername(),
                admin.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
    }

    // CREATE - Register
    public AuthResponse register(RegisterRequest request) {
        if (adminRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        if (adminRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken");
        }
        if (request.getEmployeeId() != null && !request.getEmployeeId().isEmpty()
                && adminRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw new RuntimeException("Employee ID already exists");
        }

        Admin admin = new Admin();
        admin.setFirstName(request.getFirstName());
        admin.setLastName(request.getLastName());
        admin.setEmail(request.getEmail());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setPhone(request.getPhone());
        admin.setUsername(request.getUsername());
        admin.setDepartment(request.getDepartment());
        admin.setEmployeeId(request.getEmployeeId());
        admin.setActive(true);

        Admin saved = adminRepository.save(admin);
        String token = jwtUtil.generateToken(saved.getUsername());
        return new AuthResponse(token, toProfileResponse(saved), "Registration successful");
    }

    // READ - Login
    public AuthResponse login(LoginRequest request) {
        Admin admin = adminRepository.findByUsername(request.getUsername())
                .or(() -> adminRepository.findByEmail(request.getUsername()))
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        if (!admin.isActive()) {
            throw new RuntimeException("Account is deactivated");
        }

        if (request.getOtp() == null || request.getOtp().isEmpty()) {
            twoFactorService.generateAndSendOtp(admin.getUsername(), "ADMIN", admin.getEmail());
            AuthResponse response = new AuthResponse();
            response.setMessage("OTP sent to your email");
            return response;
        } else {
            boolean isOtpValid = twoFactorService.verifyOtp(admin.getUsername(), "ADMIN", request.getOtp());
            if (!isOtpValid) {
                throw new RuntimeException("Invalid or expired OTP");
            }
            String token = jwtUtil.generateToken(admin.getUsername());
            return new AuthResponse(token, toProfileResponse(admin), "Login successful");
        }
    }

    // READ - Get Profile
    public AdminProfileResponse getProfile(String username) {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        return toProfileResponse(admin);
    }

    // UPDATE - Update Profile
    public AdminProfileResponse updateProfile(String username, UpdateRequest request) {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        if (request.getFirstName() != null) admin.setFirstName(request.getFirstName());
        if (request.getLastName() != null) admin.setLastName(request.getLastName());
        if (request.getPhone() != null) admin.setPhone(request.getPhone());
        if (request.getDepartment() != null) admin.setDepartment(request.getDepartment());

        if (request.getEmail() != null && !request.getEmail().equals(admin.getEmail())) {
            if (adminRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already in use");
            }
            admin.setEmail(request.getEmail());
        }

        if (request.getNewPassword() != null && !request.getNewPassword().isEmpty()) {
            if (request.getCurrentPassword() == null ||
                    !passwordEncoder.matches(request.getCurrentPassword(), admin.getPassword())) {
                throw new RuntimeException("Current password is incorrect");
            }
            admin.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        Admin updated = adminRepository.save(admin);
        return toProfileResponse(updated);
    }

    // DELETE - Delete Profile
    public void deleteProfile(String username) {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        adminRepository.delete(admin);
    }

    // READ - Get All Admins
    public List<AdminProfileResponse> getAllAdmins() {
        return adminRepository.findAll().stream()
                .map(this::toProfileResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    // UPDATE - Deactivate Admin
    public AdminProfileResponse deactivateAdmin(Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        admin.setActive(false);
        return toProfileResponse(adminRepository.save(admin));
    }

    // UPDATE - Reactivate Admin
    public AdminProfileResponse reactivateAdmin(Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        admin.setActive(true);
        return toProfileResponse(adminRepository.save(admin));
    }

    private AdminProfileResponse toProfileResponse(Admin admin) {
        AdminProfileResponse response = new AdminProfileResponse();
        response.setId(admin.getId());
        response.setFirstName(admin.getFirstName());
        response.setLastName(admin.getLastName());
        response.setEmail(admin.getEmail());
        response.setPhone(admin.getPhone());
        response.setUsername(admin.getUsername());
        response.setDepartment(admin.getDepartment());
        response.setEmployeeId(admin.getEmployeeId());
        response.setProfileImage(admin.getProfileImage());
        response.setActive(admin.isActive());
        response.setCreatedAt(admin.getCreatedAt() != null ? admin.getCreatedAt().toString() : null);
        response.setUpdatedAt(admin.getUpdatedAt() != null ? admin.getUpdatedAt().toString() : null);
        return response;
    }
}
