package com.MarketPlace.Service;

import com.MarketPlace.Model.*;
import com.MarketPlace.Repository.OrderRepository;
import com.MarketPlace.Repository.ProductRepository;
import com.MarketPlace.Repository.UserRepository;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository; this.productRepository = productRepository; this.userRepository = userRepository;
    }

    // create order with shipping address & payment
    public Order createOrder(Long buyerId, List<OrderItem> items, Address shippingAddress, PaymentInfo.Method method, String paymentDetails) {
        User buyer = userRepository.findById(buyerId).orElseThrow();
        double total = 0;
        List<OrderItem> persisted = new ArrayList<>();
        for (OrderItem item : items) {
            Product product = productRepository.findById(item.getProduct().getId()).orElseThrow();
            if (product.getStock() < item.getQuantity()) throw new RuntimeException("Insufficient stock for " + product.getTitle());
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
            OrderItem copy = OrderItem.builder().product(product).quantity(item.getQuantity()).price(product.getPrice()).build();
            persisted.add(copy);
            total += product.getPrice() * item.getQuantity();
        }

        PaymentInfo pay = PaymentInfo.builder()
                .method(method)
                .amount(total)
                .status(method == PaymentInfo.Method.COD ? PaymentInfo.Status.PENDING : PaymentInfo.Status.PENDING)
                .transactionId(method == PaymentInfo.Method.COD ? null : UUID.randomUUID().toString())
                .paidAt(method == PaymentInfo.Method.COD ? null : Instant.now())
                .build();

        Instant now = Instant.now();
        Instant expectedDelivery = now.plusSeconds(3 * 24 * 3600); // 3 days default

        Order order = Order.builder()
                .buyer(buyer)
                .items(persisted)
                .total(total)
                .status("CREATED")
                .placedAt(now)
                .expectedDeliveryAt(expectedDelivery)
                .shippingAddress(shippingAddress)
                .payment(pay)
                .build();

        return orderRepository.save(order);
    }

    public List<Order> listAll() {
        return orderRepository.findAll();
    }

    public List<Order> listByBuyer(Long buyerId) {
        User buyer = userRepository.findById(buyerId).orElseThrow();
        return orderRepository.findByBuyer(buyer);
    }

    // simulate payment update (for online payments)
    public PaymentInfo updatePayment(Long orderId, PaymentInfo.Status newStatus, String txId) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        PaymentInfo pay = order.getPayment();
        pay.setStatus(newStatus);
        if (txId != null) pay.setTransactionId(txId);
        if (newStatus == PaymentInfo.Status.SUCCESS) pay.setPaidAt(Instant.now());
        order.setPayment(pay);
        orderRepository.save(order);
        return pay;
    }
}
