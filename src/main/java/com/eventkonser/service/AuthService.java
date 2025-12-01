package com.eventkonser.service;

import com.eventkonser.dto.AuthResponse;
import com.eventkonser.dto.LoginRequest;
import com.eventkonser.dto.RegisterRequest;
import com.eventkonser.config.JwtTokenProvider;
import com.eventkonser.model.User;
import com.eventkonser.model.Role;
import com.eventkonser.repository.UserRepository;
import com.eventkonser.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service untuk authentication (login, register, token refresh)
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    /**
     * Login user dan return JWT token
     */
    public AuthResponse login(LoginRequest loginRequest) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getEmailOrUsername(),
                loginRequest.getPassword()
            )
        );

        // Get user from database
        User user = userRepository.findByEmail(loginRequest.getEmailOrUsername())
            .orElseThrow(() -> new ResourceNotFoundException("User tidak ditemukan"));

        // Generate tokens
        String token = jwtTokenProvider.generateTokenFromUsername(
            user.getEmail(),
            user.getRole().name(),
            user.getIdPengguna()
        );

        String refreshToken = jwtTokenProvider.generateRefreshToken(
            user.getEmail(),
            user.getIdPengguna()
        );

        return AuthResponse.builder()
            .token(token)
            .refreshToken(refreshToken)
            .userId(user.getIdPengguna())
            .username(user.getEmail())
            .email(user.getEmail())
            .nama(user.getNama())
            .role(user.getRole().name())
            .message("Login berhasil")
            .build();
    }

    /**
     * Register user baru
     */
    public AuthResponse register(RegisterRequest registerRequest) {
        // Validate password match
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new IllegalArgumentException("Password dan konfirmasi password tidak cocok");
        }

        // Check if user already exists
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email sudah terdaftar");
        }

        // Create new user
        User user = new User();
        user.setNama(registerRequest.getNama());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setNoHp(registerRequest.getNoTelepon());
        user.setAlamat(registerRequest.getAlamat());
        user.setRole(Role.USER);

        // Save user
        User savedUser = userRepository.save(user);

        // Generate tokens
        String token = jwtTokenProvider.generateTokenFromUsername(
            savedUser.getEmail(),
            savedUser.getRole().name(),
            savedUser.getIdPengguna()
        );

        String refreshToken = jwtTokenProvider.generateRefreshToken(
            savedUser.getEmail(),
            savedUser.getIdPengguna()
        );

        return AuthResponse.builder()
            .token(token)
            .refreshToken(refreshToken)
            .userId(savedUser.getIdPengguna())
            .username(savedUser.getEmail())
            .email(savedUser.getEmail())
            .nama(savedUser.getNama())
            .role(savedUser.getRole().name())
            .message("Registrasi berhasil")
            .build();
    }

    /**
     * Refresh JWT token using refresh token
     */
    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Refresh token tidak valid atau sudah kadaluarsa");
        }

        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
        Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User tidak ditemukan"));

        String newToken = jwtTokenProvider.generateTokenFromUsername(
            user.getEmail(),
            user.getRole().name(),
            user.getIdPengguna()
        );

        String newRefreshToken = jwtTokenProvider.generateRefreshToken(
            user.getEmail(),
            user.getIdPengguna()
        );

        return AuthResponse.builder()
            .token(newToken)
            .refreshToken(newRefreshToken)
            .userId(user.getIdPengguna())
            .username(user.getEmail())
            .email(user.getEmail())
            .nama(user.getNama())
            .role(user.getRole().name())
            .message("Token refreshed")
            .build();
    }

    /**
     * Get current authenticated user
     */
    @Transactional(readOnly = true)
    public User getCurrentUser(String username) {
        return userRepository.findByEmail(username)
            .orElseThrow(() -> new ResourceNotFoundException("User tidak ditemukan"));
    }
}
