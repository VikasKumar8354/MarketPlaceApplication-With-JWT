package com.MarketPlace.Controller;

import com.MarketPlace.DTOs.CreateProductDto;
import com.MarketPlace.Model.Category;
import com.MarketPlace.Model.Product;
import com.MarketPlace.Repository.CategoryRepository;
import com.MarketPlace.Service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryRepository categoryRepository;
    private final ObjectMapper objectMapper;

    public ProductController(ProductService productService,
                             CategoryRepository categoryRepository,
                             ObjectMapper objectMapper) {
        this.productService = productService;
        this.categoryRepository = categoryRepository;
        this.objectMapper = objectMapper;
    }

    @PostMapping(value = "/create", consumes = "multipart/form-data")
    @PreAuthorize("hasAnyRole('VENDOR','ADMIN')")
    public ResponseEntity<?> create(
            @AuthenticationPrincipal String subject,
            @RequestPart("data") String dtoJson,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {

        Long actorId = Long.parseLong(subject);

        CreateProductDto dto = convert(dtoJson);

        Category category = categoryRepository.findById(dto.getCategoryId()).orElse(null);

        Product product = Product.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .category(category)
                .build();

        return ResponseEntity.ok(productService.createProduct(actorId, product, imageFile));
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    @PreAuthorize("hasAnyRole('VENDOR','ADMIN')")
    public ResponseEntity<?> update(
            @AuthenticationPrincipal String subject,
            @PathVariable Long id,
            @RequestPart("data") String dtoJson,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {

        Long actorId = Long.parseLong(subject);

        CreateProductDto dto = convert(dtoJson);

        Product updated = Product.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .category(categoryRepository.findById(dto.getCategoryId()).orElse(null))
                .build();

        return ResponseEntity.ok(productService.updateProduct(actorId, id, updated, imageFile));
    }

    private CreateProductDto convert(String json) {
        try {
            return objectMapper.readValue(json, CreateProductDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Invalid JSON: " + json);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('VENDOR','ADMIN')")
    public ResponseEntity<?> delete(@AuthenticationPrincipal String subject, @PathVariable Long id) {
        Long actorId = Long.parseLong(subject);
        productService.deleteProduct(actorId, id);
        return ResponseEntity.ok().build();
    }

    private CreateProductDto convertToDto(String json) {
        try {
            return objectMapper.readValue(json, CreateProductDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Invalid JSON format: " + json);
        }
    }
}
