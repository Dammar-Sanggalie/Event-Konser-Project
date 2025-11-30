package com.eventkonser.service;

import com.eventkonser.model.User;
import com.eventkonser.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String emailOrUsername) throws UsernameNotFoundException {
        // 1. Logika Utama: Cari user berdasarkan EMAIL
        // (Walaupun parameter namanya 'username', kita cari di kolom email)
        User user = userRepository.findByEmail(emailOrUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User tidak ditemukan dengan email: " + emailOrUsername));

        // 2. Logika Safety: Handle Role (Anti-Null)
        // Mencegah Error 500 jika ada user yang kolom role-nya tidak sengaja kosong
        String roleName = "USER"; // Default fallback
        if (user.getRole() != null) {
            roleName = user.getRole().name();
        }

        // 3. Return ke Spring Security
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),      // Gunakan Email sebagai Username sistem
                user.getPassword(),   // Password Hash dari DB
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + roleName))
        );
    }
}