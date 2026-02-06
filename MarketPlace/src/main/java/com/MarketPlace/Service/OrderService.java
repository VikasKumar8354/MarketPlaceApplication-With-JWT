package com.MarketPlace.Service;

import com.MarketPlace.DTOs.OrderResponseDto;
import com.MarketPlace.Model.*;
import com.MarketPlace.Repository.OrderRepository;
import com.MarketPlace.Repository.ProductRepository;
import com.MarketPlace.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    /** CREATE ORDER **/
    public OrderResponseDto createOrder(Long buyerId, List<OrderItem> items, Address shippingAddress, PaymentInfo.Method method, String paymentDetails) {
        User buyer = userRepository.findById(buyerId).orElseThrow(() -> new RuntimeException("Buyer not found"));

        double total = 0;
        List<OrderItem> persistedItems = new ArrayList<>();

        for (OrderItem item : items) {
            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProduct().getId()));

            if (product.getStock() < item.getQuantity())
                throw new RuntimeException("Insufficient stock for " + product.getTitle());

            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(item.getQuantity())
                    .price(product.getPrice()) // snapshot price
                    .build();

            persistedItems.add(orderItem);
            total += product.getPrice() * item.getQuantity();
        }

        // Payment info
        PaymentInfo payment = PaymentInfo.builder()
                .method(method)
                .amount(total)
                .status(PaymentInfo.Status.PENDING)
                .transactionId(method == PaymentInfo.Method.COD ? null : UUID.randomUUID().toString())
                .paidAt(method == PaymentInfo.Method.COD ? null : Instant.now())
                .build();

        Instant now = Instant.now();
        Instant expectedDelivery = now.plusSeconds(3 * 24 * 3600); // 3 days

        Order order = Order.builder()
                .buyer(buyer)
                .items(persistedItems)
                .total(total)
                .status("CREATED")
                .placedAt(now)
                .expectedDeliveryAt(expectedDelivery)
                .shippingAddress(shippingAddress)
                .payment(payment)
                .build();

        Order savedOrder = orderRepository.save(order);
        return mapToDto(savedOrder);
    }

    /** LIST ALL ORDERS **/
    public List<OrderResponseDto> listAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /** LIST ORDERS BY BUYER **/
    public List<OrderResponseDto> listOrdersByBuyer(Long buyerId) {
        User buyer = userRepository.findById(buyerId).orElseThrow(() -> new RuntimeException("Buyer not found"));
        return orderRepository.findByBuyer(buyer).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /** UPDATE PAYMENT **/
    public OrderResponseDto updatePayment(Long orderId, PaymentInfo.Status status, String txId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

        PaymentInfo payment = order.getPayment();
        payment.setStatus(status);
        if (txId != null) payment.setTransactionId(txId);
        if (status == PaymentInfo.Status.SUCCESS) payment.setPaidAt(Instant.now());
        order.setPayment(payment);

        Order savedOrder = orderRepository.save(order);
        return mapToDto(savedOrder);
    }

    /** HELPER: MAP ORDER -> DTO **/
    private OrderResponseDto mapToDto(Order order) {
        List<OrderResponseDto.Item> items = order.getItems().stream().map(i ->
                OrderResponseDto.Item.builder()
                        .productId(i.getProduct().getId())
                        .productName(i.getProduct().getTitle())
                        .quantity(i.getQuantity())
                        .price(i.getPrice())
                        .build()
        ).toList();

        OrderResponseDto.AddressDto address = OrderResponseDto.AddressDto.builder()
                .label(order.getShippingAddress().getLabel())
                .line1(order.getShippingAddress().getLine1())
                .line2(order.getShippingAddress().getLine2())
                .city(order.getShippingAddress().getCity())
                .state(order.getShippingAddress().getState())
                .postalCode(order.getShippingAddress().getPostalCode())
                .country(order.getShippingAddress().getCountry())
                .phone(order.getShippingAddress().getPhone())
                .build();

        PaymentInfo pay = order.getPayment();
        OrderResponseDto.PaymentDto paymentDto = OrderResponseDto.PaymentDto.builder()
                .method(pay.getMethod().name())
                .status(pay.getStatus().name())
                .amount(pay.getAmount())
                .transactionId(pay.getTransactionId())
                .paidAt(pay.getPaidAt())
                .build();

        return OrderResponseDto.builder()
                .orderId(order.getId())
                .total(order.getTotal())
                .status(order.getStatus())
                .placedAt(order.getPlacedAt())
                .expectedDeliveryAt(order.getExpectedDeliveryAt())
                .items(items)
                .shippingAddress(address)
                .payment(paymentDto)
                .build();
    }
}
