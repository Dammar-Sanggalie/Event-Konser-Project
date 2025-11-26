package com.eventkonser.service;

import com.eventkonser.model.User;
import com.eventkonser.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

/**
 * Custom UserDetailsService untuk Spring Security authentication
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Load user by username (email atau username)
     * Dipanggil oleh Spring Security saat authentication
     */
    @Override
    public UserDetails loadUserByUsername(String emailOrUsername) throws UsernameNotFoundException {
        // Try find by email
        User user = userRepository.findByEmail(emailOrUsername)
            .orElseThrow(() -> new UsernameNotFoundException("User tidak ditemukan dengan email: " + emailOrUsername));

        return new CustomUserPrincipal(user);
    }

    /**
     * Custom UserPrincipal class untuk carry user details dan authorities
     */
    public static class CustomUserPrincipal implements UserDetails {

        private final User user;

        public CustomUserPrincipal(User user) {
            this.user = user;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return Collections.singleton(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
            );
        }

        @Override
        public String getPassword() {
            return user.getPassword();
        }

        @Override
        public String getUsername() {
            return user.getEmail();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        public User getUser() {
            return user;
        }

        public Long getUserId() {
            return user.getIdPengguna();
        }
    }
}
