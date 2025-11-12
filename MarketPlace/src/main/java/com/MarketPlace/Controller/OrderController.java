package com.MarketPlace.Controller;

import com.MarketPlace.DTOs.OrderDto;
import com.MarketPlace.Model.Order;
import com.MarketPlace.Model.OrderItem;
import com.MarketPlace.Model.Product;
import com.MarketPlace.Service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    public OrderController(OrderService orderService) { this.orderService = orderService; }

    @PostMapping("/create")
    public ResponseEntity<?> create(@AuthenticationPrincipal String subject, @RequestBody OrderDto orderDto) {
        Long buyerId = Long.parseLong(subject);

        List<OrderItem> items = orderDto.getItems().stream()
                .map(i -> OrderItem.builder()
                        .product(Product.builder().id(i.productId()).build())
                        .quantity(i.quantity())
                        .build())
                .collect(Collectors.toList());

        Order order = orderService.createOrder(buyerId, items);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/list")
    public List<Order> listAll() { return orderService.listAll(); }

    @GetMapping("/buyer")
    public List<Order> listByBuyer(@AuthenticationPrincipal String subject) {
        Long buyerId = Long.parseLong(subject);
        return orderService.listByBuyer(buyerId);
    }
}
