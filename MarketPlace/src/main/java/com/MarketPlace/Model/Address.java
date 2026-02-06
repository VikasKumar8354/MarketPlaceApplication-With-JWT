package com.MarketPlace.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String label;      // Home, Office, etc.
    private String line1;      // Street / Building
    private String line2;      // Optional
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String phone;

    // Optional: link to user if you want saved addresses
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
