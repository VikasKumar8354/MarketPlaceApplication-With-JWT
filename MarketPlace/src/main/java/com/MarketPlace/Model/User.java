package com.MarketPlace.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String shopName;

    private boolean vendorVerified;

//    public User() {}
//
//    public User(Long id, String name, String email, Role role, String password, String shopName, boolean vendorVerified) {
//        this.id = id;
//        this.name = name;
//        this.email = email;
//        this.role = role;
//        this.password = password;
//        this.shopName = shopName;
//        this.vendorVerified = vendorVerified;
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public Role getRole() {
//        return role;
//    }
//
//    public void setRole(Role role) {
//        this.role = role;
//    }
//
//    public String getShopName() {
//        return shopName;
//    }
//
//    public void setShopName(String shopName) {
//        this.shopName = shopName;
//    }
//
//    public boolean isVendorVerified() {
//        return vendorVerified;
//    }
//
//    public void setVendorVerified(boolean vendorVerified) {
//        this.vendorVerified = vendorVerified;
//    }
}
