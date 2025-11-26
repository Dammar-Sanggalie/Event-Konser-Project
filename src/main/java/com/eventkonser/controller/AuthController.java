package com.eventkonser.controller;

import com.eventkonser.dto.ApiResponse;
import com.eventkonser.dto.AuthResponse;
import com.eventkonser.dto.LoginRequest;
import com.eventkonser.dto.RegisterRequest;
import com.eventkonser.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller untuk Authentication (Login, Register, Refresh Token)
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/login - Login user dengan email/username dan password
     * Request: { emailOrUsername: "user@example.com", password: "password123" }
     * Response: { token, refreshToken, userId, username, email, nama, role }
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest, BindingResult bindingResult) {
        try {
            // Check for validation errors
            if (bindingResult.hasErrors()) {
                String errorMessages = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
                return ResponseEntity.badRequest().body(
                    ApiResponse.error(errorMessages)
                );
            }

            AuthResponse authResponse = authService.login(loginRequest);
            return ResponseEntity.ok(ApiResponse.success("Login berhasil", authResponse));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse.error("Email/Username atau password salah")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.error("Login gagal: " + e.getMessage())
            );
        }
    }

    /**
     * POST /api/auth/register - Register user baru
     * Request: { nama, email, username, password, confirmPassword, noTelepon, alamat }
     * Response: { token, refreshToken, userId, username, email, nama, role }
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest registerRequest, BindingResult bindingResult) {
        try {
            // Check for validation errors
            if (bindingResult.hasErrors()) {
                String errorMessages = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
                return ResponseEntity.badRequest().body(
                    ApiResponse.error(errorMessages)
                );
            }

            AuthResponse authResponse = authService.register(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Registrasi berhasil", authResponse)
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                ApiResponse.error(e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.error("Registrasi gagal: " + e.getMessage())
            );
        }
    }

    /**
     * POST /api/auth/refresh - Refresh JWT token
     * Request: { refreshToken: "..." }
     * Response: { token, refreshToken, userId, username, email, nama, role }
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@RequestBody Map<String, String> request) {
        try {
            String refreshToken = request.get("refreshToken");
            if (refreshToken == null || refreshToken.isEmpty()) {
                return ResponseEntity.badRequest().body(
                    ApiResponse.error("Refresh token tidak diberikan")
                );
            }

            AuthResponse authResponse = authService.refreshToken(refreshToken);
            return ResponseEntity.ok(ApiResponse.success("Token refreshed", authResponse));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse.error(e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.error("Refresh token gagal: " + e.getMessage())
            );
        }
    }

    /**
     * GET /api/auth/me - Get current authenticated user info
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Object>> getCurrentUser() {
        try {
            // Get from SecurityContext
            String username = org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

            Object userInfo = authService.getCurrentUser(username);
            return ResponseEntity.ok(ApiResponse.success("Current user info", userInfo));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse.error("Failed to get user info: " + e.getMessage())
            );
        }
    }

    /**
     * POST /api/auth/logout - Logout user (frontend responsibility to clear token)
     * Backend tidak perlu do anything karena menggunakan stateless JWT
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        return ResponseEntity.ok(ApiResponse.success("Logout berhasil, silahkan hapus token di client", null));
    }
}
