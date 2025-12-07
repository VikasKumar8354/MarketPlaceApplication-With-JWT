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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository,
                          UserRepository userRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    // Store image and return path
    private String saveImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            // 1️⃣ ABSOLUTE PATH (Windows)

            String basePath = "C:/MarketPlace/uploads/products/";

            // 2️⃣ Create directory if not exists
            File directory = new File(basePath);
            if (!directory.exists()) {
                directory.mkdirs(); // VERY IMPORTANT
            }

            // 3️⃣ Create unique filename
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            // 4️⃣ Save file
            File savedFile = new File(basePath + fileName);
            file.transferTo(savedFile);

            // 5️⃣ Return path for DB
            return "/uploads/products/" + fileName;

        } catch (Exception e) {
            throw new RuntimeException("Image upload failed", e);
        }
    }


    public Product createProduct(Long actorId, Product product, MultipartFile imageFile) {

        User actor = userRepository.findById(actorId).orElseThrow();

        if (actor.getRole() == Role.USER)
            throw new RuntimeException("USER not allowed to create products");

        if (actor.getRole() == Role.VENDOR && !actor.isVendorVerified())
            throw new RuntimeException("Vendor not verified");

        if (actor.getRole() == Role.VENDOR)
            product.setVendor(actor);

        if (imageFile != null && !imageFile.isEmpty())
            product.setImagePath(saveImage(imageFile));

        return productRepository.save(product);
    }

    public Page<Product> listAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public Product updateProduct(Long actorId, Long productId,
                                 Product updated, MultipartFile image) {

        User actor = userRepository.findById(actorId).orElseThrow();
        Product existing = productRepository.findById(productId).orElseThrow();

        if (actor.getRole() == Role.VENDOR &&
                !existing.getVendor().getId().equals(actor.getId()))
            throw new RuntimeException("Vendor cannot update other vendors' product");

        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setPrice(updated.getPrice());
        existing.setStock(updated.getStock());
        existing.setCategory(updated.getCategory());

        if (image != null && !image.isEmpty())
            existing.setImagePath(saveImage(image));

        return productRepository.save(existing);
    }

    public void deleteProduct(Long actorId, Long productId) {
        User actor = userRepository.findById(actorId).orElseThrow();
        Product existing = productRepository.findById(productId).orElseThrow();

        if (actor.getRole() == Role.VENDOR &&
                !existing.getVendor().getId().equals(actor.getId()))
            throw new RuntimeException("Vendor cannot delete other vendors' product");

        productRepository.delete(existing);
    }

}
