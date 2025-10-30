package com.eventkonser.repository;

import com.eventkonser.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    // Find by name
    Optional<Category> findByNamaKategori(String namaKategori);
    
    // Check if exists
    boolean existsByNamaKategori(String namaKategori);
}