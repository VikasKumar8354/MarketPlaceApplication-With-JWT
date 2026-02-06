package com.MarketPlace.Service;

import com.MarketPlace.DTOs.ResetPasswordRequest;
import com.MarketPlace.Model.User;
import com.MarketPlace.Repository.UserRepository;
import com.MarketPlace.SecurityConfiguration.JwtService;
import com.MarketPlace.emailService.EmailService;
import com.MarketPlace.emailService.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserAuthService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OtpService otpService;

    // Send OTP
    public void sendOtp(String email) {
        User user = repo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        String otp = otpService.generateOtp(email);
        emailService.sendOtp(email, otp); // send via email
    }

    // Reset password
    public void resetPassword(ResetPasswordRequest req) {
        boolean valid = otpService.verifyOtp(req.getEmail(), req.getOtp());
        if (!valid) throw new RuntimeException("Invalid or expired OTP");

        User user = repo.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(encoder.encode(req.getNewPassword()));
        repo.save(user);
    }

}
