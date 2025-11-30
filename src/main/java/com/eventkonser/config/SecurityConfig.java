package com.eventkonser.config;

import com.eventkonser.service.CustomUserDetailsService; // Pastikan Import service buatanmu
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security Configuration dengan JWT authentication (Spring Security 6)
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
    securedEnabled = true,
    jsr250Enabled = true,
    prePostEnabled = true
)
@RequiredArgsConstructor
public class SecurityConfig {

    // UBAH TIPE INI: Dari 'UserDetailsService' (generic) ke 'CustomUserDetailsService' (spesifik)
    // Supaya Spring 100% yakin pakai file yang ada logika "Email as Username" tadi.
    private final CustomUserDetailsService userDetailsService;
    
    private final JwtTokenProvider tokenProvider;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(tokenProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        
        // Ini akan menghubungkan CustomUserDetailsService kamu dengan sistem login
        authProvider.setUserDetailsService(userDetailsService);
        
        // Ini akan menghubungkan BCrypt dengan sistem login
        authProvider.setPasswordEncoder(passwordEncoder());
        
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. PENTING: JANGAN DISABLE CORS KALAU FRONTEND BEDA PORT (Misal 8080 vs 5500)
            // Gunakan defaults agar browser tidak memblokir request Javascript
            .cors(Customizer.withDefaults()) 
            
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // Static resources
                .requestMatchers("/", "/favicon.ico", "/index.html", "/css/**", "/js/**", "/assets/**", "/components/**", "/*.html").permitAll()

                // AUTH ENDPOINTS (Wajib Public)
                .requestMatchers("/api/auth/**").permitAll() 
                .requestMatchers("/api/login/**").permitAll()
                .requestMatchers("/api/register/**").permitAll()

                // API LAINNYA
                // Saat development, kita permitAll dulu biar tidak pusing permission
                // Nanti kalau login sudah sukses, baru kita ketatkan lagi
                .requestMatchers("/api/**").permitAll()
                
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}