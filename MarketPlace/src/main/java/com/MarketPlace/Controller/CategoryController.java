package com.MarketPlace.Controller;

import com.MarketPlace.Model.Category;
import com.MarketPlace.Repository.CategoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryRepository catRepo;
    public CategoryController(CategoryRepository catRepo) { this.catRepo = catRepo; }

    @GetMapping("/list")
    public List<Category> list() { return catRepo.findAll(); }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody Category c) {
        return ResponseEntity.ok(catRepo.save(c));
    }
}
