package com.MarketPlace.Controller;

import com.MarketPlace.DTOs.OrderDto;
import com.MarketPlace.DTOs.OrderResponseDto;
import com.MarketPlace.Model.*;
import com.MarketPlace.Service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/createOrder")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<OrderResponseDto> createOrder(@AuthenticationPrincipal String subject, @RequestBody OrderDto dto) {
        Long buyerId = Long.parseLong(subject);
        return ResponseEntity.ok(orderService.createOrder(
                buyerId,
                dto.getItems().stream().map(item -> OrderItem.builder()
                        .product(Product.builder().id(item.productId()).build())
                        .quantity(item.quantity())
                        .build()).toList(),
                Address.builder()
                        .label(dto.getAddressLabel())
                        .line1(dto.getLine1())
                        .line2(dto.getLine2())
                        .city(dto.getCity())
                        .state(dto.getState())
                        .postalCode(dto.getPostalCode())
                        .country(dto.getCountry())
                        .phone(dto.getPhone())
                        .build(),
                PaymentInfo.Method.valueOf(dto.getPaymentMethod()),
                dto.getPaymentDetails()
        ));
    }

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN','VENDOR')")
    public ResponseEntity<List<OrderResponseDto>> listAll() {
        return ResponseEntity.ok(orderService.listAllOrders());
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<OrderResponseDto>> myOrders(@AuthenticationPrincipal String subject) {
        return ResponseEntity.ok(orderService.listOrdersByBuyer(Long.parseLong(subject)));
    }

    @PostMapping("/{orderId}/payment")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<OrderResponseDto> updatePayment(@PathVariable Long orderId, @RequestParam String status, @RequestParam(required = false) String txId) {
        return ResponseEntity.ok(orderService.updatePayment(orderId, PaymentInfo.Status.valueOf(status), txId));
    }
}
