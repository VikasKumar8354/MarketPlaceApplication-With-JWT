package com.MarketPlace.Service;

import com.MarketPlace.Model.Product;
import com.MarketPlace.Model.Role;
import com.MarketPlace.Model.User;
import com.MarketPlace.Repository.CategoryRepository;
import com.MarketPlace.Repository.ProductRepository;
import com.MarketPlace.Repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository prodRepo;
    private final CategoryRepository catRepo;
    private final UserRepository userRepo;

    public ProductService(ProductRepository prodRepo, CategoryRepository catRepo, UserRepository userRepo) {
        this.prodRepo = prodRepo; this.catRepo = catRepo; this.userRepo = userRepo;
    }

    public Product createProduct(Long actorId, Product p) {
        User actor = userRepo.findById(actorId).orElseThrow();
        if (actor.getRole() == Role.USER) throw new RuntimeException("USER cannot create products");
        if (actor.getRole() == Role.VENDOR && !actor.isVendorVerified()) throw new RuntimeException("Vendor not verified");
        if (actor.getRole() == Role.VENDOR) p.setVendor(actor);
        return prodRepo.save(p);
    }

    public Page<Product> listAll(Pageable page) { return prodRepo.findAll(page); }
    public Optional<Product> findById(Long id) { return prodRepo.findById(id); }
    public Page<Product> findByVendor(Long vendorId, Pageable page) {
        User vendor = userRepo.findById(vendorId).orElseThrow();
        return prodRepo.findByVendor(vendor, page);
    }

    public Product updateProduct(Long actorId, Long productId, Product updated) {
        User actor = userRepo.findById(actorId).orElseThrow();
        Product existing = prodRepo.findById(productId).orElseThrow();
        if (actor.getRole() == Role.VENDOR && !existing.getVendor().getId().equals(actor.getId()))
            throw new RuntimeException("Vendor can only update own products");
        if (actor.getRole() == Role.USER) throw new RuntimeException("Not authorized");
        existing.setTitle(updated.getTitle()); existing.setDescription(updated.getDescription());
        existing.setPrice(updated.getPrice()); existing.setStock(updated.getStock());
        existing.setImageUrl(updated.getImageUrl());
        if (updated.getCategory() != null) existing.setCategory(updated.getCategory());
        return prodRepo.save(existing);
    }

    public void deleteProduct(Long actorId, Long productId) {
        User actor = userRepo.findById(actorId).orElseThrow();
        Product existing = prodRepo.findById(productId).orElseThrow();
        if (actor.getRole() == Role.VENDOR && !existing.getVendor().getId().equals(actor.getId()))
            throw new RuntimeException("Vendor can only delete own products");
        if (actor.getRole() == Role.USER) throw new RuntimeException("Not authorized");
        prodRepo.deleteById(productId);
    }
}
