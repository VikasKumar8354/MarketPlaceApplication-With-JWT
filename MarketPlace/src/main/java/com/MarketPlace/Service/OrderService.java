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

    private final OrderRepository orderRepo;
    private final ProductRepository productRepo;
    private final UserRepository userRepo;

    public OrderService(OrderRepository orderRepo, ProductRepository productRepo, UserRepository userRepo) {
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
        this.userRepo = userRepo;
    }

    public Order createOrder(Long buyerId, List<OrderItem> items) {
        var buyer = userRepo.findById(buyerId).orElseThrow();
        double total = 0;
        List<OrderItem> savedItems = new ArrayList<>();
        for (OrderItem item : items) {
            var product = productRepo.findById(item.getProduct().getId()).orElseThrow();
            if (product.getStock() < item.getQuantity()) throw new RuntimeException("Insufficient stock for " + product.getTitle());
            product.setStock(product.getStock() - item.getQuantity());
            productRepo.save(product);

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
        return orderRepo.save(order);
    }

    public List<Order> listAll() { return orderRepo.findAll(); }

    public List<Order> listByBuyer(Long buyerId) {
        var buyer = userRepo.findById(buyerId).orElseThrow();
        return orderRepo.findByBuyer(buyer);
    }
}
