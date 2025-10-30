package com.eventkonser.service;

import com.eventkonser.model.Category;
import com.eventkonser.repository.CategoryRepository;
import com.eventkonser.exception.ResourceNotFoundException;
import com.eventkonser.exception.DuplicateResourceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Kategori tidak ditemukan dengan ID: " + id));
    }
    
    @Transactional
    public Category createCategory(Category category) {
        if (categoryRepository.existsByNamaKategori(category.getNamaKategori())) {
            throw new DuplicateResourceException("Kategori sudah ada: " + category.getNamaKategori());
        }
        return categoryRepository.save(category);
    }
    
    @Transactional
    public Category updateCategory(Long id, Category categoryDetails) {
        Category category = getCategoryById(id);
        category.setNamaKategori(categoryDetails.getNamaKategori());
        category.setDeskripsi(categoryDetails.getDeskripsi());
        return categoryRepository.save(category);
    }
    
    @Transactional
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
    }
}
