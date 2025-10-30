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
}