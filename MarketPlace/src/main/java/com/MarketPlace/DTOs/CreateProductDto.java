package com.MarketPlace.DTOs;

import lombok.Data;

@Data
public class CreateProductDto {

    private String title;
    private String description;
    private Double price;
    private Integer stock;
    private Long categoryId;
    private String imageUrl;
}
