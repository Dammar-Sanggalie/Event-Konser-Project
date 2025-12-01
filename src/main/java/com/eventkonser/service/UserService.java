package com.eventkonser.service;

import com.eventkonser.model.User;
import com.eventkonser.model.Role;
import com.eventkonser.repository.UserRepository;
import com.eventkonser.exception.ResourceNotFoundException;
import com.eventkonser.exception.DuplicateResourceException;
import com.eventkonser.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public long countAllUsers() {
        try {
            // Gunakan count query yang lebih efficient
            long count = userRepository.count();
            log.info("Total users count: {}", count);
            return count;
        } catch (Exception e) {
            log.error("Error counting all users: ", e);
            return 0L;
        }
    }
    
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User tidak ditemukan dengan ID: " + id));
    }
    
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User tidak ditemukan dengan email: " + email));
    }
    
    @Transactional(readOnly = true)
    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }
    
    @Transactional(readOnly = true)
    public List<User> searchUsers(String keyword) {
        return userRepository.findByNamaContainingIgnoreCase(keyword);
    }
    
    @Transactional
    public User createUser(User user) {
        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateResourceException("Email sudah terdaftar: " + user.getEmail());
        }
        
        // TODO: Hash password dengan BCrypt
        // user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        return userRepository.save(user);
    }
    
    @Transactional
    public User updateUser(Long id, User userDetails) {
        User user = getUserById(id);
        
        // Check if new email already exists (and not same user)
        if (!user.getEmail().equals(userDetails.getEmail()) && 
            userRepository.existsByEmail(userDetails.getEmail())) {
            throw new DuplicateResourceException("Email sudah digunakan: " + userDetails.getEmail());
        }
        
        user.setNama(userDetails.getNama());
        user.setEmail(userDetails.getEmail());
        user.setNoHp(userDetails.getNoHp());
        user.setAlamat(userDetails.getAlamat());
        
        return userRepository.save(user);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }
    
    @Transactional(readOnly = true)
    public long countUsersByRole(Role role) {
        return userRepository.countByRole(role);
    }
    
    /**
     * Get all users as UserResponse DTO (without circular references)
     * Using native query to bypass Hibernate enum validation
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsersAsResponse() {
        return userRepository.findAllAsRawData().stream()
                .map(this::convertRawDataToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert raw user data to UserResponse DTO
     */
    private UserResponse convertRawDataToResponse(UserRepository.UserRawData rawData) {
        UserResponse response = new UserResponse();
        response.setIdPengguna(rawData.getIdPengguna());
        response.setNama(rawData.getNama());
        response.setEmail(rawData.getEmail());
        response.setNoHp(rawData.getNoHp());
        response.setRole(rawData.getRole()); // Keep as raw String from database
        response.setAlamat(rawData.getAlamat());
        response.setCreatedAt(rawData.getCreatedAt());
        response.setUpdatedAt(rawData.getUpdatedAt());
        return response;
    }
    
    /**
     * Update user role only (for admin panel)
     */
    @Transactional
    public User updateUserRole(Long userId, String newRole) {
        User user = getUserById(userId);
        try {
            Role role = Role.valueOf(newRole.toUpperCase());
            user.setRole(role);
            return userRepository.save(user);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role: " + newRole);
        }
    }
}
