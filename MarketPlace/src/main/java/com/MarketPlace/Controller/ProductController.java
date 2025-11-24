package com.MarketPlace.Controller;

import com.MarketPlace.DTOs.CreateProductDto;
import com.MarketPlace.Model.Category;
import com.MarketPlace.Model.Product;
import com.MarketPlace.Repository.CategoryRepository;
import com.MarketPlace.Service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryRepository categoryRepository;

    public ProductController(ProductService productService, CategoryRepository categoryRepository) {
        this.productService = productService; this.categoryRepository = categoryRepository;
    }

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('VENDOR','ADMIN')")
    public Page<Product> list(@RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size) {
        return productService.listAll(PageRequest.of(page,size));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('VENDOR','ADMIN')")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return productService.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('VENDOR','ADMIN')")
    public ResponseEntity<?> create(@AuthenticationPrincipal String subject, @RequestBody CreateProductDto dto) {
        Long actorId = Long.parseLong(subject);
        Category category = null;
        if (dto.getCategoryId() != null) category = categoryRepository.findById(dto.getCategoryId()).orElse(null);
        Product product = Product.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .imageUrl(dto.getImageUrl())
                .category(category)
                .build();
        Product created = productService.createProduct(actorId, product);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('VENDOR','ADMIN')")
    public ResponseEntity<?> update(@AuthenticationPrincipal String subject, @PathVariable Long id, @RequestBody CreateProductDto dto) {
        Long actorId = Long.parseLong(subject);
        Product updated = Product.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .imageUrl(dto.getImageUrl())
                .build();
        if (dto.getCategoryId() != null) updated.setCategory(categoryRepository.findById(dto.getCategoryId()).orElse(null));
        return ResponseEntity.ok(productService.updateProduct(actorId, id, updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('VENDOR','ADMIN')")
    public ResponseEntity<?> delete(@AuthenticationPrincipal String subject, @PathVariable Long id) {
        Long actorId = Long.parseLong(subject);
        productService.deleteProduct(actorId, id);
        return ResponseEntity.ok().build();
    }
}
