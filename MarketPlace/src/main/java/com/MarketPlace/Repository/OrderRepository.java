package com.MarketPlace.Repository;

import com.MarketPlace.Model.Order;
import com.MarketPlace.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findByBuyer(User buyer);
}
