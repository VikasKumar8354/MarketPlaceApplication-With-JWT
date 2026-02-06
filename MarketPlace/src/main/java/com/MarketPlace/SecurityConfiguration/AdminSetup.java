package com.MarketPlace.SecurityConfiguration;

import com.MarketPlace.Model.Role;
import com.MarketPlace.Model.User;
import com.MarketPlace.Repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
public class AdminSetup {

    @Bean
    CommandLineRunner createAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String adminEmail = "raj20028354@gmail.com";

            if (userRepository.findByEmail(adminEmail).isEmpty()) {
                User admin = new User();
                admin.setName("Admin");
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode("Saanvi@123"));
                admin.setRole(Role.ADMIN);
                admin.setShopName("Admin Shop");
                userRepository.save(admin);

                System.out.println("Admin created successfully");
            } else {
                System.out.println("ℹ️ ADMIN ALREADY EXISTS");
            }
        };
    }
}



