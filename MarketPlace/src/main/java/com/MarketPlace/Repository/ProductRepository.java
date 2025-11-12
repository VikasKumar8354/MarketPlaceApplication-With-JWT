package com.MarketPlace.Repository;


import com.MarketPlace.Model.Category;
import com.MarketPlace.Model.Product;
import com.MarketPlace.Model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProductRepository extends JpaRepository<Product,Long> {
    Page<Product> findByCategory(Category category, Pageable pageable);
    Page<Product> findByVendor(User vendor, Pageable pageable);
}
