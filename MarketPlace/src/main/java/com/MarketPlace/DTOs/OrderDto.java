package com.MarketPlace.DTOs;

import lombok.Data;

import java.util.List;
@Data
public class OrderDto {

    public static record Item(Long productId, Integer quantity) {}
    private List<Item> items;
    private PaymentDto payment; // null for COD? provide PaymentDto with method=COD
    private Long expectedDeliveryDays; // optional: number of days to add to placedAt
}
