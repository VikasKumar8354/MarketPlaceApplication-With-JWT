package com.MarketPlace.Controller;

import com.MarketPlace.Model.Role;
import com.MarketPlace.Model.User;
import com.MarketPlace.Service.UserAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserAuthService userService;

    public UserController(UserAuthService userService) { this.userService = userService; }

    @GetMapping("/list")
    public List<User> listAll() { return userService.listAll(); }

    @GetMapping("/role/{role}")
    public List<User> listByRole(@PathVariable Role role) { return userService.findByRole(role); }

    // actorId must be passed as authenticated user's id (subject). For convenience here: use AuthenticationPrincipal
    @PostMapping("/{id}/assign-vendor")
    public ResponseEntity<?> assignVendor(@AuthenticationPrincipal String subject, @PathVariable Long id,
                                          @RequestParam String shopName) {
        Long actorId = Long.parseLong(subject);
        User v = userService.assignVendor(actorId, id, shopName);
        return ResponseEntity.ok(v);
    }

    @PostMapping("/{id}/verify-vendor")
    public ResponseEntity<?> verifyVendor(@AuthenticationPrincipal String subject, @PathVariable Long id) {
        Long actorId = Long.parseLong(subject);
        return ResponseEntity.ok(userService.verifyVendor(actorId, id));
    }
}
