package com.MarketPlace.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User buyer;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Payment payment;

    private Double total;

    private String status; // CREATED, CONFIRMED, SHIPPED, DELIVERED, CANCELLED

    private Instant placedAt; // order creation time
    private Instant deliveryDate; // expected or actual delivery date
}
