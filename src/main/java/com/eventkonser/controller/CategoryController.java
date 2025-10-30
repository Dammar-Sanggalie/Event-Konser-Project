package com.eventkonser.controller;

import com.eventkonser.model.Category;
import com.eventkonser.service.CategoryService;
import com.eventkonser.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CategoryController {
    
    private final CategoryService categoryService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Category>>> getAllCategories() {
        return ResponseEntity.ok(ApiResponse.success("Success", categoryService.getAllCategories()));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Category>> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Success", categoryService.getCategoryById(id)));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<Category>> createCategory(@RequestBody Category category) {
        return ResponseEntity.ok(ApiResponse.success("Kategori berhasil dibuat", categoryService.createCategory(category)));
    }
}




