package com.MarketPlace.Service;

import com.MarketPlace.Model.Order;
import com.MarketPlace.Model.OrderItem;
import com.MarketPlace.Repository.OrderRepository;
import com.MarketPlace.Repository.ProductRepository;
import com.MarketPlace.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
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

    public Order createOrder(Long buyerId, List<OrderItem> items) {
        var buyer = userRepository.findById(buyerId).orElseThrow();
        double total = 0;
        List<OrderItem> savedItems = new ArrayList<>();
        for (OrderItem item : items) {
            var product = productRepository.findById(item.getProduct().getId()).orElseThrow();
            if (product.getStock() < item.getQuantity()) throw new RuntimeException("Insufficient stock for " + product.getTitle());
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);

            var copy = OrderItem.builder()
                    .product(product)
                    .quantity(item.getQuantity())
                    .price(product.getPrice())
                    .build();
            savedItems.add(copy);
            total += product.getPrice() * item.getQuantity();
        }
        Order order = Order.builder()
                .buyer(buyer)
                .items(savedItems)
                .total(total)
                .status("CREATED")
                .createdAt(Instant.now())
                .build();
        return orderRepository.save(order);
    }

    public List<Order> listAll() {
        return orderRepository.findAll();
    }

    public List<Order> listByBuyer(Long buyerId) {
        var buyer = userRepository.findById(buyerId).orElseThrow();
        return orderRepository.findByBuyer(buyer);
    }
}
