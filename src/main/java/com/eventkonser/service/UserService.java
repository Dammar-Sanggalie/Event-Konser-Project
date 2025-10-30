package com.eventkonser.service;

import com.eventkonser.model.User;
import com.eventkonser.model.Role;
import com.eventkonser.repository.UserRepository;
import com.eventkonser.exception.ResourceNotFoundException;
import com.eventkonser.exception.DuplicateResourceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
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
}
