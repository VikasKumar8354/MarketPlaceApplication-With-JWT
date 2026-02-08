package com.MarketPlace.Controller;

import com.MarketPlace.emailService.OtpService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final OtpService otpService;

    public AuthController(OtpService otpService) {
        this.otpService = otpService;
    }

    @PostMapping("/sendOtp")
    public String sendOtp(@RequestParam String phone) {
        String otp = otpService.generateOtp(phone);

        // Ideally you send OTP to the phone via SMS API, not return it
        return "OTP sent to " + phone;
    }

    @PostMapping("/verifyOtp")
    public String verifyOtp(@RequestParam String phone, @RequestParam String otp) {
        boolean valid = otpService.verifyOtp(phone, otp);
        return valid ? "OTP Verified" : "Invalid or expired OTP";
    }
}
