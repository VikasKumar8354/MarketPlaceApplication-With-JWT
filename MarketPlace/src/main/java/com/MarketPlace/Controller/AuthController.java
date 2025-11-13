package com.MarketPlace.Controller;

import com.MarketPlace.DTOs.AuthRequest;
import com.MarketPlace.DTOs.AuthResponse;
import com.MarketPlace.DTOs.CreateUserDto;
import com.MarketPlace.Model.User;
import com.MarketPlace.Service.UserAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserAuthService userAuthService;

    public AuthController(UserAuthService userAuthService) { this.userAuthService = userAuthService; }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody CreateUserDto dto) {
        User user = userAuthService.register(dto);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            String token = userAuthService.login(authRequest.getEmail(), authRequest.getPassword());
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (Exception ex) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }
}
