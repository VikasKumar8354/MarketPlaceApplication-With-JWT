package com.MarketPlace.Service;

import com.MarketPlace.DTOs.CreateUserDto;
import com.MarketPlace.Model.Address;
import com.MarketPlace.Model.Role;
import com.MarketPlace.Model.User;
import com.MarketPlace.Repository.AddressRepository;
import com.MarketPlace.Repository.UserRepository;
import com.MarketPlace.SecurityConfiguration.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserAuthService {

    private final UserRepository userRepo;
    private final AddressRepository addressRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserAuthService(UserRepository userRepo, AddressRepository addressRepo, PasswordEncoder passwordEncoder , JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.addressRepo = addressRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public User register(CreateUserDto dto) {
        if (userRepo.findByEmail(dto.getEmail()).isPresent()) throw new RuntimeException("Email already used");
        User u = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(dto.getRole() != null ? dto.getRole() : Role.USER)
                .shopName(dto.getShopName())
                .vendorVerified(false)
                .build();
        if (dto.getRole() == Role.USER) u.setVendorVerified(false);
        return userRepo.save(u);
    }

    public String login(String email, String rawPassword) {
        User u = userRepo.findByEmail(email).orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        if (!passwordEncoder.matches(rawPassword, u.getPassword())) throw new BadCredentialsException("Invalid credentials");
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", u.getRole().name());
        claims.put("email", u.getEmail());
        return jwtUtil.generateToken(String.valueOf(u.getId()), claims);
    }

    public Optional<User> findById(Long id) { return userRepo.findById(id); }
    public Optional<User> findByEmail(String email) { return userRepo.findByEmail(email); }
    public List<User> listAll() { return userRepo.findAll(); }
    public List<User> findByRole(Role role) { return userRepo.findByRole(role); }

    public User addAddress(Long userId, Address address) {
        User u = userRepo.findById(userId).orElseThrow();
        Address saved = addressRepo.save(address);
        var list = u.getAddresses();
        if (list == null) list = new ArrayList<>();
        list.add(saved);
        u.setAddresses(list);
        return userRepo.save(u);
    }

    public User assignVendor(Long actorId, Long targetId, String shopName) {
        User actor = userRepo.findById(actorId).orElseThrow();
        if (actor.getRole() != Role.ADMIN) throw new RuntimeException("Only ADMIN can assign vendor");
        User target = userRepo.findById(targetId).orElseThrow();
        target.setRole(Role.VENDOR);
        target.setShopName(shopName);
        target.setVendorVerified(false);
        return userRepo.save(target);
    }

    public User verifyVendor(Long actorId, Long vendorId) {

        User actor = userRepo.findById(actorId).orElseThrow();
        if (actor.getRole() != Role.ADMIN) throw new RuntimeException("Only ADMIN can verify vendor");
        User vendor = userRepo.findById(vendorId).orElseThrow();
        if (vendor.getRole() != Role.VENDOR) throw new RuntimeException("Not a vendor");
        vendor.setVendorVerified(true);
        return userRepo.save(vendor);
    }

}
