package com.MarketPlace.Service;

import com.MarketPlace.Model.Product;
import com.MarketPlace.Model.Role;
import com.MarketPlace.Repository.CategoryRepository;
import com.MarketPlace.Repository.ProductRepository;
import com.MarketPlace.Repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public Product createProduct(Long actorId, Product p) {
        var actor = userRepository.findById(actorId).orElseThrow();
        if (actor.getRole() == Role.USER) throw new RuntimeException("USER cannot create products");
        if (actor.getRole() == Role.VENDOR && !actor.isVendorVerified()) throw new RuntimeException("Vendor not verified");
        if (actor.getRole() == Role.VENDOR) p.setVendor(actor);
        return productRepository.save(p);
    }

    public Page<Product> listAll(Pageable pageable) { return productRepository.findAll(pageable); }
    public Optional<Product> findById(Long id) { return productRepository.findById(id); }
    public Page<Product> findByVendor(Long vendorId, Pageable pageable) {
        var vendor = userRepository.findById(vendorId).orElseThrow();
        return productRepository.findByVendor(vendor, pageable);
    }

    public Product updateProduct(Long actorId, Long productId, Product updated) {
        var actor = userRepository.findById(actorId).orElseThrow();
        var existing = productRepository.findById(productId).orElseThrow();
        if (actor.getRole() == Role.VENDOR && !existing.getVendor().getId().equals(actor.getId()))
            throw new RuntimeException("Vendor may only update own products");
        if (actor.getRole() == Role.USER) throw new RuntimeException("Not authorized");
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setPrice(updated.getPrice());
        existing.setStock(updated.getStock());
        existing.setImageUrl(updated.getImageUrl());
        if (updated.getCategory() != null) existing.setCategory(updated.getCategory());
        return productRepository.save(existing);
    }

    public void deleteProduct(Long actorId, Long productId) {
        var actor = userRepository.findById(actorId).orElseThrow();
        var existing = productRepository.findById(productId).orElseThrow();
        if (actor.getRole() == Role.VENDOR && !existing.getVendor().getId().equals(actor.getId()))
            throw new RuntimeException("Vendor may only delete own products");
        if (actor.getRole() == Role.USER) throw new RuntimeException("Not authorized");
        productRepository.deleteById(productId);
    }
}
