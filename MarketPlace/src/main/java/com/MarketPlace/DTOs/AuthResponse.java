package com.MarketPlace.DTOs;

import com.MarketPlace.Model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {

    private Long userId;
    private Role role;
    private String token;
}
