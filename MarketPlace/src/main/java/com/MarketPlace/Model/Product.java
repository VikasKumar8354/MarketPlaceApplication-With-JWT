package com.MarketPlace.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 2000)
    private String description;

    private Double price;
    private Integer stock;
    private String imageUrl;

    @ManyToOne
    private Category category;

    @ManyToOne
    private User vendor;
}
