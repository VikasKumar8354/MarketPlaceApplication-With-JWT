package com.MarketPlace.Controller;

import com.MarketPlace.DTOs.AuthRequest;
import com.MarketPlace.DTOs.AuthResponse;
import com.MarketPlace.DTOs.CreateUserDto;
import com.MarketPlace.Model.User;
import com.MarketPlace.Service.UserAuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserAuthService userService;
    public AuthController(UserAuthService userService) { this.userService = userService; }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody CreateUserDto dto) {
        User u = userService.register(dto);
        return ResponseEntity.ok(u);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        try {
            String token = userService.login(req.getEmail(), req.getPassword());
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (Exception ex) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }
}
