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

    private final OrderRepository orderRepo;
    private final ProductRepository productRepo;
    private final UserRepository userRepo;

    public OrderService(OrderRepository orderRepo, ProductRepository productRepo, UserRepository userRepo) {
        this.orderRepo = orderRepo; this.productRepo = productRepo; this.userRepo = userRepo;
    }

    // create order with shipping address & payment
    public Order createOrder(Long buyerId, List<OrderItem> items, Address shippingAddress, PaymentInfo.Method method, String paymentDetails) {
        User buyer = userRepo.findById(buyerId).orElseThrow();
        double total = 0;
        List<OrderItem> persisted = new ArrayList<>();
        for (OrderItem it : items) {
            Product p = productRepo.findById(it.getProduct().getId()).orElseThrow();
            if (p.getStock() < it.getQuantity()) throw new RuntimeException("Insufficient stock for " + p.getTitle());
            p.setStock(p.getStock() - it.getQuantity());
            productRepo.save(p);
            OrderItem copy = OrderItem.builder().product(p).quantity(it.getQuantity()).price(p.getPrice()).build();
            persisted.add(copy);
            total += p.getPrice() * it.getQuantity();
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

        return orderRepo.save(order);
    }

    public List<Order> listAll() { return orderRepo.findAll(); }

    public List<Order> listByBuyer(Long buyerId) {
        User buyer = userRepo.findById(buyerId).orElseThrow();
        return orderRepo.findByBuyer(buyer);
    }

    // simulate payment update (for online payments)
    public PaymentInfo updatePayment(Long orderId, PaymentInfo.Status newStatus, String txId) {
        Order order = orderRepo.findById(orderId).orElseThrow();
        PaymentInfo pay = order.getPayment();
        pay.setStatus(newStatus);
        if (txId != null) pay.setTransactionId(txId);
        if (newStatus == PaymentInfo.Status.SUCCESS) pay.setPaidAt(Instant.now());
        order.setPayment(pay);
        orderRepo.save(order);
        return pay;
    }
}
