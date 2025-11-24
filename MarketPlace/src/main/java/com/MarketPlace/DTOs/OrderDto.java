package com.MarketPlace.DTOs;

import lombok.Data;
import java.util.List;

@Data
public class OrderDto {

    public static record Item(Long productId, Integer quantity) {}
    private List<Item> items;

    // shipping address: reuse fields
    private String addressLabel;
    private String line1;
    private String line2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String phone;

    // payment
    private String paymentMethod; // COD | UPI | CARD | NET_BANKING
    // for simulation, if UPI/CARD etc you may provide extra info (optional)
    private String paymentDetails;
}
