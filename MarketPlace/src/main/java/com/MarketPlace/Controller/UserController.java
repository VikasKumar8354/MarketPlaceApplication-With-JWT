package com.MarketPlace.Controller;

import com.MarketPlace.Model.Address;
import com.MarketPlace.Model.Role;
import com.MarketPlace.Model.User;
import com.MarketPlace.Service.UserAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserAuthService userAuthService;
    public UserController(UserAuthService userAuthService) { this.userAuthService = userAuthService; }

    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> listAll() { return userAuthService.listAll(); }

    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> byRole(@PathVariable Role role) { return userAuthService.findByRole(role); }

    @PostMapping("/address")
    @PreAuthorize("hasAnyRole('USER','VENDOR','ADMIN')")
    public ResponseEntity<?> addAddress(@AuthenticationPrincipal String subject, @RequestBody Address address) {
        Long userId = Long.parseLong(subject);
        User user = userAuthService.addAddress(userId, address);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/{id}/assign-vendor")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assignVendor(@PathVariable Long id, @RequestParam String shopName, @AuthenticationPrincipal String subject) {
        Long actorId = Long.parseLong(subject);
        User user = userAuthService.assignVendor(actorId, id, shopName);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/{id}/verify-vendor")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> verifyVendor(@PathVariable Long id, @AuthenticationPrincipal String subject) {
        Long actorId = Long.parseLong(subject);
        User user = userAuthService.verifyVendor(actorId, id);
        return ResponseEntity.ok(user);
    }
}
