package com.example.demo.controller;

import com.example.demo.dto.PasswordResetRequest;
import com.example.demo.dto.PasswordResetResponse;
import com.example.demo.model.Admin;
import com.example.demo.model.Manager;
import com.example.demo.model.StaffOperator;
import com.example.demo.model.Technician;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.ManagerRepository;
import com.example.demo.repository.StaffOperatorRepository;
import com.example.demo.repository.TechnicianRepository;
import com.example.demo.service.PasswordPolicyService;
import com.example.demo.service.TwoFactorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/password-reset")
@CrossOrigin(origins = "*")
public class PasswordResetController {

    @Autowired private AdminRepository adminRepo;
    @Autowired private ManagerRepository managerRepo;
    @Autowired private StaffOperatorRepository staffRepo;
    @Autowired private TechnicianRepository techRepo;

    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private PasswordPolicyService passwordPolicy;
    @Autowired private TwoFactorService twoFactorService;
    @Autowired(required = false) private JavaMailSender mailSender;

    @PostMapping("/send-code")
    public ResponseEntity<?> sendCode(@RequestBody Map<String, String> body) {
        try {
            String userType = required(body.get("userType"), "User type is required");
            String username = required(body.get("username"), "Username is required");
            String email = required(body.get("email"), "Email is required");

            String actualEmail = resolveAndValidateEmail(userType, username, email);

            twoFactorService.generateAndSendOtp(username, userType.toUpperCase(), actualEmail);

            return ResponseEntity.ok(Map.of(
                    "message", "Verification code sent",
                    "email", maskEmail(actualEmail)
            ));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> reset(@Valid @RequestBody PasswordResetRequest req, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(new PasswordResetResponse(false, errorMsg));
        }

        try {
            if (!req.getNewPassword().equals(req.getConfirmPassword())) {
                throw new RuntimeException("New password and confirm password do not match");
            }

            // validate username + email matches a real user
            resolveAndValidateEmail(req.getUserType(), req.getUsername(), req.getEmail());

            // validate 2FA code
            boolean isOtpValid = twoFactorService.verifyOtp(req.getUsername(), req.getUserType().toUpperCase(), req.getTwoFactorCode());
            if (!isOtpValid) {
                throw new RuntimeException("Invalid or expired 2FA code");
            }

            // password policy
            passwordPolicy.validateStrength(req.getNewPassword());

            switch (req.getUserType().toUpperCase()) {
                case "ADMIN" -> {
                    Admin a = adminRepo.findByUsername(req.getUsername())
                            .or(() -> adminRepo.findByEmail(req.getUsername()))
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    passwordPolicy.validateNotSameAsCurrent(passwordEncoder, req.getNewPassword(), a.getPassword());
                    a.setPassword(passwordEncoder.encode(req.getNewPassword()));
                    adminRepo.save(a);
                }
                case "MANAGER" -> {
                    Manager m = managerRepo.findByUsername(req.getUsername())
                            .or(() -> managerRepo.findByEmail(req.getUsername()))
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    passwordPolicy.validateNotSameAsCurrent(passwordEncoder, req.getNewPassword(), m.getPassword());
                    m.setPassword(passwordEncoder.encode(req.getNewPassword()));
                    managerRepo.save(m);
                }
                case "STAFF" -> {
                    StaffOperator s = staffRepo.findByUsername(req.getUsername())
                            .or(() -> staffRepo.findByEmail(req.getUsername()))
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    passwordPolicy.validateNotSameAsCurrent(passwordEncoder, req.getNewPassword(), s.getPassword());
                    s.setPassword(passwordEncoder.encode(req.getNewPassword()));
                    staffRepo.save(s);
                }
                case "TECH" , "TECHNICIAN" -> {
                    Technician t = techRepo.findByUsername(req.getUsername())
                            .or(() -> techRepo.findByEmail(req.getUsername()))
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    passwordPolicy.validateNotSameAsCurrent(passwordEncoder, req.getNewPassword(), t.getPassword());
                    t.setPassword(passwordEncoder.encode(req.getNewPassword()));
                    techRepo.save(t);
                }
                default -> throw new RuntimeException("Invalid user type");
            }

            return ResponseEntity.ok(new PasswordResetResponse(true, "Password updated successfully"));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(new PasswordResetResponse(false, ex.getMessage()));
        }
    }

    private String resolveAndValidateEmail(String userType, String username, String providedEmail) {
        String actualEmail;
        switch (userType.toUpperCase()) {
            case "ADMIN" -> {
                Admin a = adminRepo.findByUsername(username)
                        .or(() -> adminRepo.findByEmail(username))
                        .orElseThrow(() -> new RuntimeException("Invalid username"));
                actualEmail = a.getEmail();
            }
            case "MANAGER" -> {
                Manager m = managerRepo.findByUsername(username)
                        .or(() -> managerRepo.findByEmail(username))
                        .orElseThrow(() -> new RuntimeException("Invalid username"));
                actualEmail = m.getEmail();
            }
            case "STAFF" -> {
                StaffOperator s = staffRepo.findByUsername(username)
                        .or(() -> staffRepo.findByEmail(username))
                        .orElseThrow(() -> new RuntimeException("Invalid username"));
                actualEmail = s.getEmail();
            }
            case "TECH", "TECHNICIAN" -> {
                Technician t = techRepo.findByUsername(username)
                        .or(() -> techRepo.findByEmail(username))
                        .orElseThrow(() -> new RuntimeException("Invalid username"));
                actualEmail = t.getEmail();
            }
            default -> throw new RuntimeException("Invalid user type");
        }

        if (!actualEmail.equalsIgnoreCase(providedEmail)) {
            throw new RuntimeException("Email does not match the username");
        }
        return actualEmail;
    }

    private String required(String v, String msg) {
        if (v == null || v.isBlank()) throw new RuntimeException(msg);
        return v;
    }

    private String maskEmail(String email) {
        int at = email.indexOf('@');
        if (at <= 1) return "***" + email.substring(at);
        return email.substring(0, 1) + "***" + email.substring(at - 1);
    }
}
