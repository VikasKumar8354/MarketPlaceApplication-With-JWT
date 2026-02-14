package com.MarketPlace.Service;


import com.MarketPlace.Model.Address;
import com.MarketPlace.Model.Role;
import com.MarketPlace.Model.User;
import com.MarketPlace.Repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

     // ✅ LIST ALL USERS (ADMIN)
    public List<User> listAll() {
        return userRepository.findAll();
    }

    // ✅ FIND BY ROLE (ADMIN)
    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }

    // ✅ ADD ADDRESS
    public User addAddress(Long userId, Address address) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        address.setUser(user); // relation set
        user.getAddresses().add(address);

        return userRepository.save(user);
    }

    // ✅ ASSIGN VENDOR (ADMIN ACTION)
    public User assignVendor(Long actorId, Long targetUserId, String shopName) {

        User admin = userRepository.findById(actorId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        if (admin.getRole() != Role.ADMIN)
            throw new RuntimeException("Only ADMIN allowed");

        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("Target user not found"));

        target.setRole(Role.VENDOR);
        target.setShopName(shopName);

        return userRepository.save(target);
    }

    // ✅ VERIFY VENDOR (ADMIN)
    public User verifyVendor(Long actorId, Long vendorId) {

        User admin = userRepository.findById(actorId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        if (admin.getRole() != Role.ADMIN)
            throw new RuntimeException("Only ADMIN allowed");

        User vendor = userRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        if (vendor.getRole() != Role.VENDOR)
            throw new RuntimeException("User is not vendor");

        vendor.setVendorVerified(true);

        return userRepository.save(vendor);
    }
}

