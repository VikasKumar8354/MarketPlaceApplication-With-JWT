package com.MarketPlace.Service;

import com.MarketPlace.Model.*;
import com.MarketPlace.Repository.OrderRepository;
import com.MarketPlace.Repository.ProductRepository;
import com.MarketPlace.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

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

    public Order createOrder(Long buyerId, List<OrderItem> items, Payment payment, Long expectedDeliveryDays) {
        var buyer = userRepository.findById(buyerId).orElseThrow();
        double total = 0;
        List<OrderItem> savedItems = new ArrayList<>();
        for (OrderItem it : items) {
            var product = productRepository.findById(it.getProduct().getId()).orElseThrow();
            if (product.getStock() < it.getQuantity()) throw new RuntimeException("Insufficient stock for " + product.getTitle());
            product.setStock(product.getStock() - it.getQuantity());
            productRepository.save(product);

            var copy = OrderItem.builder()
                    .product(product)
                    .quantity(it.getQuantity())
                    .price(product.getPrice())
                    .build();
            savedItems.add(copy);
            total += product.getPrice() * it.getQuantity();
        }

        // validate payment amount (if provided)
        if (payment != null) {
            payment.setAmount(total);
            if (payment.getMethod() == PaymentMethod.COD) {
                payment.setStatus(PaymentStatus.PENDING);
                payment.setTransactionId("COD-" + System.currentTimeMillis());
            } else {
                // simulate online payment success
                payment.setTransactionId("TXN-" + System.currentTimeMillis());
                payment.setStatus(PaymentStatus.SUCCESS);
                payment.setPaidAt(Instant.now());
            }
        }

        Instant now = Instant.now();
        Instant delivery = expectedDeliveryDays != null ? now.plus(expectedDeliveryDays, ChronoUnit.DAYS) : null;

        Order order = Order.builder()
                .buyer(buyer)
                .items(savedItems)
                .payment(payment)
                .total(total)
                .status("CREATED")
                .placedAt(now)
                .deliveryDate(delivery)
                .build();
        return orderRepository.save(order);
    }

    public List<Order> listAll() { return orderRepository.findAll(); }

    public List<Order> listByBuyer(Long buyerId) {
        var buyer = userRepository.findById(buyerId).orElseThrow();
        return orderRepository.findByBuyer(buyer);
    }
}
