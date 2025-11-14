package com.MarketPlace.Service;

import com.MarketPlace.DTOs.CreateUserDto;
import com.MarketPlace.Model.Role;
import com.MarketPlace.Model.User;
import com.MarketPlace.Repository.UserRepository;
import com.MarketPlace.SecurityConfiguration.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserAuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // register (role optional)
    public User register(CreateUserDto dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent())
            throw new RuntimeException("Email already used");

        Role role = dto.getRole() != null ? dto.getRole() : Role.USER;

        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(role)
                .shopName(dto.getShopName())
                .vendorVerified(false)
                .build();

        return userRepository.save(user);
    }

    // login -> returns token
    public String login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) throw new BadCredentialsException("Invalid credentials");

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        claims.put("email", user.getEmail());
        return jwtUtil.generateToken(String.valueOf(user.getId()), claims);
    }

    public List<User> listAll() {
        return userRepository.findAll();
    }

    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }

    // admin actions

    public User assignVendor(Long actorId, Long targetId, String shopName) {
        User actor = userRepository.findById(actorId).orElseThrow();
        if (actor.getRole() != Role.ADMIN) throw new RuntimeException("Only ADMIN can assign vendor");
        User target = userRepository.findById(targetId).orElseThrow();
        target.setRole(Role.VENDOR);
        target.setShopName(shopName);
        target.setVendorVerified(false);
        return userRepository.save(target);
    }

    public User verifyVendor(Long actorId, Long vendorId) {

        User actor = userRepository.findById(actorId).orElseThrow();
        if (actor.getRole() != Role.ADMIN) throw new RuntimeException("Only ADMIN can verify vendor");
        User vendor = userRepository.findById(vendorId).orElseThrow();
        if (vendor.getRole() != Role.VENDOR) throw new RuntimeException("Target is not a vendor");
        vendor.setVendorVerified(true);
        return userRepository.save(vendor);
    }

}
