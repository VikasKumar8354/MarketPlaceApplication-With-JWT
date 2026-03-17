package com.MarketPlace.Controller;

import com.MarketPlace.DTOs.AuthRequest;
import com.MarketPlace.DTOs.CreateUserDto;
import com.MarketPlace.DTOs.ResetPasswordRequest;
import com.MarketPlace.Service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserAuthService service;

    // REGISTER
    @PostMapping("/register")
    public String register(@RequestBody CreateUserDto dto){

        return service.register(dto);
    }

    // LOGIN
    @PostMapping("/login")
    public String login(@RequestBody AuthRequest req){

        return service.login(req);
    }

    // SEND OTP
    @PostMapping("/forgotPassword")
    public String forgotPassword(@RequestParam String email){

        service.sendOtp(email);

        return "OTP sent to email";
    }

    // RESET PASSWORD
    @PostMapping("/resetPassword")
    public String resetPassword(@RequestBody ResetPasswordRequest req){

        service.resetPassword(req);

        return "Password Reset Successfully";
    }
}