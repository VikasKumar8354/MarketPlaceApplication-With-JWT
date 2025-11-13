package com.MarketPlace.DTOs;

import lombok.Data;

import java.util.List;
@Data
public class OrderDto {

    public static record Item(Long productId, Integer quantity) {}
    private List<Item> items;
}
