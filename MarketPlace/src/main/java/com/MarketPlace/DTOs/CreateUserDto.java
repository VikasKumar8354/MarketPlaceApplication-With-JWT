package com.MarketPlace.DTOs;

import com.MarketPlace.Model.Role;
import lombok.Data;

@Data
public class CreateUserDto {
    private String name;
    private String email;
    private String password;
    private Role role;
    private String shopName;
}
