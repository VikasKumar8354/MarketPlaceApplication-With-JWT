package com.MarketPlace.Controller;

import com.MarketPlace.DTOs.CreateProductDto;
import com.MarketPlace.Model.Category;
import com.MarketPlace.Model.Product;
import com.MarketPlace.Repository.CategoryRepository;
import com.MarketPlace.Service.ProductService;
import com.MarketPlace.Service.UserAuthService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryRepository categoryRepo;
    private final UserAuthService userAuthService;

    public ProductController(ProductService productService, CategoryRepository categoryRepo, UserAuthService userAuthService) {
        this.productService = productService;
        this.categoryRepo = categoryRepo;
        this.userAuthService = userAuthService;
    }

    @GetMapping("/list")
    public Page<Product> list(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size) {
        return productService.listAll(PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return productService.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@AuthenticationPrincipal String subject, @RequestBody CreateProductDto dto) {
        Long actorId = Long.parseLong(subject);
        Category cat = null;
        if (dto.getCategoryId() != null) cat = categoryRepo.findById(dto.getCategoryId()).orElse(null);
        Product p = Product.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .imageUrl(dto.getImageUrl())
                .category(cat)
                .build();
        Product created = productService.createProduct(actorId, p);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@AuthenticationPrincipal String subject, @PathVariable Long id, @RequestBody CreateProductDto dto) {
        Long actorId = Long.parseLong(subject);
        Product updated = Product.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .imageUrl(dto.getImageUrl())
                .build();
        if (dto.getCategoryId() != null) {
            Category cat = categoryRepo.findById(dto.getCategoryId()).orElse(null);
            updated.setCategory(cat);
        }
        return ResponseEntity.ok(productService.updateProduct(actorId, id, updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@AuthenticationPrincipal String subject, @PathVariable Long id) {
        Long actorId = Long.parseLong(subject);
        productService.deleteProduct(actorId, id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/vendor/{vendorId}")
    public Page<Product> byVendor(@PathVariable Long vendorId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return productService.findByVendor(vendorId, PageRequest.of(page, size));
    }
}
