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

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private List<OrderItem> items;

    private Double total;

    private String status; // CREATED, PROCESSING, SHIPPED, DELIVERED, CANCELLED

    private Instant placedAt;
    private Instant expectedDeliveryAt; // or placed/ship dates

    @ManyToOne(cascade = CascadeType.ALL)
    private Address shippingAddress;

    @Embedded
    private PaymentInfo payment;
}
