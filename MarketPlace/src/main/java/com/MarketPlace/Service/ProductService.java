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

    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepo;
    private final UserRepository userRepo;

    public ProductService(ProductRepository productRepo, CategoryRepository categoryRepo, UserRepository userRepo) {
        this.productRepo = productRepo;
        this.categoryRepo = categoryRepo;
        this.userRepo = userRepo;
    }

    public Product createProduct(Long actorId, Product p) {
        var actor = userRepo.findById(actorId).orElseThrow();
        if (actor.getRole() == Role.USER) throw new RuntimeException("USER cannot create products");
        if (actor.getRole() == Role.VENDOR && !actor.isVendorVerified()) throw new RuntimeException("Vendor not verified");
        if (actor.getRole() == Role.VENDOR) p.setVendor(actor);
        return productRepo.save(p);
    }

    public Page<Product> listAll(Pageable pageable) {
        return productRepo.findAll(pageable);
    }

    public Optional<Product> findById(Long id) {
        return productRepo.findById(id);
    }

    public Page<Product> findByVendor(Long vendorId, Pageable pageable) {
        var vendor = userRepo.findById(vendorId).orElseThrow();
        return productRepo.findByVendor(vendor, pageable);
    }

    public Product updateProduct(Long actorId, Long productId, Product updated) {
        var actor = userRepo.findById(actorId).orElseThrow();
        var existing = productRepo.findById(productId).orElseThrow();
        if (actor.getRole() == Role.VENDOR && !existing.getVendor().getId().equals(actor.getId()))
            throw new RuntimeException("Vendor may only update own products");
        if (actor.getRole() == Role.USER) throw new RuntimeException("Not authorized");
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setPrice(updated.getPrice());
        existing.setStock(updated.getStock());
        existing.setImageUrl(updated.getImageUrl());
        if (updated.getCategory() != null) existing.setCategory(updated.getCategory());
        return productRepo.save(existing);
    }

    public void deleteProduct(Long actorId, Long productId) {
        var actor = userRepo.findById(actorId).orElseThrow();
        var existing = productRepo.findById(productId).orElseThrow();
        if (actor.getRole() == Role.VENDOR && !existing.getVendor().getId().equals(actor.getId()))
            throw new RuntimeException("Vendor may only delete own products");
        if (actor.getRole() == Role.USER) throw new RuntimeException("Not authorized");
        productRepo.deleteById(productId);
    }
}
