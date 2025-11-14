package com.MarketPlace.Controller;

import com.MarketPlace.DTOs.OrderDto;
import com.MarketPlace.DTOs.PaymentDto;
import com.MarketPlace.Model.*;
import com.MarketPlace.Service.OrderService;
import com.MarketPlace.Service.UserAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserAuthService userAuthService;

    public OrderController(OrderService orderService, UserAuthService userAuthService) {
        this.orderService = orderService;
        this.userAuthService = userAuthService;
    }

    @PostMapping
    public ResponseEntity<?> create(@AuthenticationPrincipal String subject, @RequestBody OrderDto dto) {
        Long buyerId = Long.parseLong(subject);
        List<OrderItem> items = dto.getItems().stream().map(i ->
                OrderItem.builder().product(Product.builder().id(i.productId()).build()).quantity(i.quantity()).build()
        ).collect(Collectors.toList());

        Payment payment = null;
        if (dto.getPayment() != null) {
            PaymentDto pd = dto.getPayment();
            payment = Payment.builder()
                    .method(pd.getMethod())
                    .amount(pd.getAmount() != null ? pd.getAmount() : 0.0)
                    .build();
        } else {
            // default COD
            payment = Payment.builder().method(PaymentMethod.COD).build();
        }

        Order order = orderService.createOrder(buyerId, items, payment, dto.getExpectedDeliveryDays());
        return ResponseEntity.ok(order);
    }

    @GetMapping
    public List<Order> listAll() { return orderService.listAll(); }

    @GetMapping("/buyer")
    public List<Order> listByBuyer(@AuthenticationPrincipal String subject) {
        Long buyerId = Long.parseLong(subject);
        return orderService.listByBuyer(buyerId);
    }
}
