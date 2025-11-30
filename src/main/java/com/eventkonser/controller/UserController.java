package com.eventkonser.controller;

import com.eventkonser.model.User;
import com.eventkonser.service.UserService;
import com.eventkonser.dto.ApiResponse;
import com.eventkonser.dto.UserResponse;
import com.eventkonser.dto.UpdateUserRoleRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {
    
    private final UserService userService;
    
    /**
     * GET /api/users/{id} - Get user by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Success", userService.getUserById(id)));
    }
    
    /**
     * GET /api/users/email/{email} - Get user by email
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<User>> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(ApiResponse.success("Success", userService.getUserByEmail(email)));
    }
    
    /**
     * POST /api/users - Create new user
     */
    @PostMapping
    public ResponseEntity<ApiResponse<User>> createUser(@RequestBody User user) {
        return ResponseEntity.ok(ApiResponse.success("User berhasil dibuat", userService.createUser(user)));
    }
    
    /**
     * PUT /api/users/{id} - Update user
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(
            @PathVariable Long id,
            @RequestBody User userDetails) {
        return ResponseEntity.ok(ApiResponse.success("User berhasil diupdate", userService.updateUser(id, userDetails)));
    }
    
    /**
     * PUT /api/users/{id}/role - Update user role only (Admin only)
     */
    @PutMapping("/{id}/role")
    public ResponseEntity<ApiResponse<User>> updateUserRole(
            @PathVariable Long id,
            @RequestBody UpdateUserRoleRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Role user berhasil diupdate", userService.updateUserRole(id, request.getRole())));
    }
    
    /**
     * DELETE /api/users/{id} - Delete user (Admin only)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User berhasil dihapus", null));
    }
    
    /**
     * GET /api/users - Get all users (Admin only)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        try {
            List<UserResponse> users = userService.getAllUsersAsResponse();
            System.out.println("✅ Found " + users.size() + " users");
            return ResponseEntity.ok(ApiResponse.success("Success", users));
        } catch (Exception e) {
            System.err.println("❌ Error fetching users: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(
                ApiResponse.error("Failed to fetch users: " + e.getMessage())
            );
        }
    }
}