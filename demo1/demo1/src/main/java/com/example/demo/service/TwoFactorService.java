package com.example.demo.service;

import com.example.demo.model.TwoFactorOtp;
import com.example.demo.repository.TwoFactorOtpRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class TwoFactorService {

    @Autowired
    private TwoFactorOtpRepository twoFactorOtpRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    public void generateAndSendOtp(String username, String userType, String email) {
        // Delete existing OTPs for this user to avoid clutter
        twoFactorOtpRepository.deleteByUsernameAndUserType(username, userType);

        String otp = String.format("%06d", new Random().nextInt(999999));
        String hashedOtp = passwordEncoder.encode(otp);

        TwoFactorOtp twoFactorOtp = new TwoFactorOtp();
        twoFactorOtp.setUsername(username);
        twoFactorOtp.setUserType(userType);
        twoFactorOtp.setOtpHash(hashedOtp);
        twoFactorOtp.setCreatedAt(LocalDateTime.now());
        twoFactorOtp.setExpiryDate(LocalDateTime.now().plusMinutes(5));

        twoFactorOtpRepository.save(twoFactorOtp);

        sendOtpEmail(email, username, otp);
    }

    private void sendOtpEmail(String to, String userName, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("PrintProTrack - Login Verification");

            String htmlBody = String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body {
                            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                            background-color: #f4fbf7;
                            margin: 0;
                            padding: 0;
                        }
                        .container {
                            max-width: 600px;
                            margin: 40px auto;
                            background-color: #ffffff;
                            border-radius: 8px;
                            border: 1px solid #e8f0ec;
                            box-shadow: 0 4px 12px rgba(0,0,0,0.05);
                            overflow: hidden;
                        }
                        .header {
                            background-color: #1a2020;
                            padding: 24px;
                            text-align: center;
                            border-bottom: 3px solid #3ddc8c;
                        }
                        .header h1 {
                            color: #ffffff;
                            margin: 0;
                            font-size: 24px;
                            letter-spacing: 1px;
                        }
                        .header h1 span {
                            color: #3ddc8c;
                        }
                        .content {
                            padding: 32px 24px;
                            color: #2a3530;
                            line-height: 1.6;
                        }
                        .content p {
                            margin: 0 0 16px;
                            font-size: 16px;
                        }
                        .otp-container {
                            text-align: center;
                            margin: 32px 0;
                            padding: 20px;
                            background-color: #f4fbf7;
                            border-radius: 6px;
                            border: 1px dashed #2db870;
                        }
                        .otp-code {
                            font-size: 36px;
                            font-weight: bold;
                            color: #1e8a57;
                            letter-spacing: 6px;
                            margin: 0;
                        }
                        .footer {
                            background-color: #f4fbf7;
                            padding: 16px 24px;
                            text-align: center;
                            font-size: 13px;
                            color: #556860;
                            border-top: 1px solid #e8f0ec;
                        }
                        .warning {
                            font-size: 14px;
                            color: #556860;
                            margin-top: 24px;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>PrintPro<span>Track</span></h1>
                        </div>
                        <div class="content">
                            <p>Hello <strong>%s</strong>,</p>
                            <p>We received a request to access your account. Please use the following verification code to complete your secure sign-in:</p>
                            <div class="otp-container">
                                <p class="otp-code">%s</p>
                            </div>
                            <p class="warning"><strong>Note:</strong> This code will expire in <strong>5 minutes</strong>. If you did not request this login, please ignore this email or contact your administrator.</p>
                        </div>
                        <div class="footer">
                            &copy; 2026 PrintProTrack System. All rights reserved.
                        </div>
                    </div>
                </body>
                </html>
                """, userName, otp);

            helper.setText(htmlBody, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public boolean verifyOtp(String username, String userType, String inputOtp) {
        Optional<TwoFactorOtp> optionalOtp = twoFactorOtpRepository.findTopByUsernameAndUserTypeOrderByCreatedAtDesc(username, userType);

        if (optionalOtp.isPresent()) {
            TwoFactorOtp storedOtp = optionalOtp.get();
            if (storedOtp.getExpiryDate().isAfter(LocalDateTime.now())) {
                boolean isMatch = passwordEncoder.matches(inputOtp, storedOtp.getOtpHash());
                if (isMatch) {
                    twoFactorOtpRepository.delete(storedOtp);
                    return true;
                }
            } else {
                twoFactorOtpRepository.delete(storedOtp);
            }
        }
        return false;
    }
}
