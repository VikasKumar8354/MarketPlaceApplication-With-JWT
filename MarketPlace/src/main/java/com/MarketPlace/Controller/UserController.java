package com.MarketPlace.Controller;

import com.MarketPlace.Model.Address;
import com.MarketPlace.Model.Role;
import com.MarketPlace.Model.User;
import com.MarketPlace.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> listAll() {
        return userService.listAll();
    }

    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> byRole(@PathVariable Role role) {
        return userService.findByRole(role);
    }

    @PostMapping("/address")
    @PreAuthorize("hasAnyRole('USER','VENDOR','ADMIN')")
    public ResponseEntity<?> addAddress(
            @AuthenticationPrincipal String subject,
            @RequestBody Address address) {

        Long userId = Long.parseLong(subject);
        User user = userService.addAddress(userId, address);

        return ResponseEntity.ok(user);
    }

    @PostMapping("/{id}/assign-vendor")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assignVendor(
            @PathVariable Long id,
            @RequestParam String shopName,
            @AuthenticationPrincipal String subject) {

        Long actorId = Long.parseLong(subject);
        User user = userService.assignVendor(actorId, id, shopName);

        return ResponseEntity.ok(user);
    }

    @PostMapping("/{id}/verify-vendor")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> verifyVendor(
            @PathVariable Long id,
            @AuthenticationPrincipal String subject) {

        Long actorId = Long.parseLong(subject);
        User user = userService.verifyVendor(actorId, id);

        return ResponseEntity.ok(user);
    }
}
