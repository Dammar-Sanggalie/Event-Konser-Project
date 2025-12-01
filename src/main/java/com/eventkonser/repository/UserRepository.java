package com.eventkonser.repository;

import com.eventkonser.model.User;
import com.eventkonser.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Find by email (untuk login)
    Optional<User> findByEmail(String email);
    
    // Check if email exists
    boolean existsByEmail(String email);
    
    // Find by role
    List<User> findByRole(Role role);
    
    // Search users by name
    List<User> findByNamaContainingIgnoreCase(String keyword);
    
    // Get user with order statistics
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.orders WHERE u.idPengguna = :userId")
    Optional<User> findByIdWithOrders(@Param("userId") Long userId);
    
    // Count users by role
    long countByRole(Role role);
    
    // Get all users as native query to bypass enum validation
    @Query(value = "SELECT id_pengguna as idPengguna, nama, email, no_hp as noHp, role, alamat, created_at as createdAt, updated_at as updatedAt FROM pengguna", nativeQuery = true)
    List<UserRawData> findAllAsRawData();
    
    // Interface for raw user data mapping
    interface UserRawData {
        Long getIdPengguna();
        String getNama();
        String getEmail();
        String getNoHp();
        String getRole();
        String getAlamat();
        java.time.LocalDateTime getCreatedAt();
        java.time.LocalDateTime getUpdatedAt();
    }
}