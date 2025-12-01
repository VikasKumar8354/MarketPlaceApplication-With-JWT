package com.MarketPlace.Controller;

import com.MarketPlace.DTOs.OrderDto;
import com.MarketPlace.Model.*;
import com.MarketPlace.Service.OrderService;
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

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/createOrder")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createOrder(@AuthenticationPrincipal String subject, @RequestBody OrderDto dto) {
        System.out.println(subject);
        Long buyerId = Long.parseLong(subject);
        List<OrderItem> items = dto.getItems().stream().map(item ->
                OrderItem.builder().product(Product.builder().id(item.productId()).build()).quantity(item.quantity()).build()
        ).collect(Collectors.toList());

        Address address = Address.builder()
                .label(dto.getAddressLabel())
                .line1(dto.getLine1())
                .line2(dto.getLine2())
                .city(dto.getCity())
                .state(dto.getState())
                .postalCode(dto.getPostalCode())
                .country(dto.getCountry())
                .phone(dto.getPhone())
                .build();

        PaymentInfo.Method method = PaymentInfo.Method.valueOf(dto.getPaymentMethod());
        Order order = orderService.createOrder(buyerId, items, address, method, dto.getPaymentDetails());
        return ResponseEntity.ok(order);
    }

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN','VENDOR')")
    public List<Order> listAll() {
        return orderService.listAll();
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public List<Order> myOrders(@AuthenticationPrincipal String subject) {
        Long buyerId = Long.parseLong(subject);
        return orderService.listByBuyer(buyerId);
    }

    // simulate payment callback/update (admin or buyer)
    @PostMapping("/{orderId}/payment")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<?> updatePayment(@PathVariable Long orderId, @RequestParam String status, @RequestParam(required=false) String txId) {
        PaymentInfo.Status sts = PaymentInfo.Status.valueOf(status);
        PaymentInfo updated = orderService.updatePayment(orderId, sts, txId);
        return ResponseEntity.ok(updated);
    }
}
