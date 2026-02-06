package com.MarketPlace.DTOs;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class OrderResponseDto {

    private Long orderId;
    private Double total;
    private String status;
    private Instant placedAt;
    private Instant expectedDeliveryAt;

    private List<Item> items;
    private AddressDto shippingAddress;
    private PaymentDto payment;

    @Data
    @Builder
    public static class Item {
        private Long productId;
        private String productName;
        private Integer quantity;
        private Double price;
    }

    @Data
    @Builder
    public static class AddressDto {
        private String label;
        private String line1;
        private String line2;
        private String city;
        private String state;
        private String postalCode;
        private String country;
        private String phone;
    }

    @Data
    @Builder
    public static class PaymentDto {
        private String method;
        private String status;
        private Double amount;
        private String transactionId;
        private Instant paidAt;
    }
}
