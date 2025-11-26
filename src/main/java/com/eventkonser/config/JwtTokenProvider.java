package com.eventkonser.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT Token Provider untuk generate, validate, dan extract claims dari JWT token
 */
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${app.jwtSecret:mySecretKeyForEventKonserProjectWithVeryLongStringToMakeItSecureEnough}")
    private String jwtSecret;

    @Value("${app.jwtExpirationMs:86400000}") // 24 hours default
    private long jwtExpirationMs;

    @Value("${app.jwtRefreshExpirationMs:604800000}") // 7 days default
    private long jwtRefreshExpirationMs;

    /**
     * Generate JWT token dari Authentication
     */
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        String role = authentication.getAuthorities().stream()
            .findFirst()
            .map(auth -> auth.getAuthority())
            .orElse("ROLE_USER");

        return Jwts.builder()
            .setSubject(username)
            .claim("role", role)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
            .signWith(getSigningKey(), SignatureAlgorithm.HS512)
            .compact();
    }

    /**
     * Generate JWT token dari username dan role
     */
    public String generateTokenFromUsername(String username, String role, Long userId) {
        return Jwts.builder()
            .setSubject(username)
            .claim("role", role)
            .claim("userId", userId)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
            .signWith(getSigningKey(), SignatureAlgorithm.HS512)
            .compact();
    }

    /**
     * Generate refresh token (longer expiration)
     */
    public String generateRefreshToken(String username, Long userId) {
        return Jwts.builder()
            .setSubject(username)
            .claim("userId", userId)
            .claim("type", "REFRESH")
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + jwtRefreshExpirationMs))
            .signWith(getSigningKey(), SignatureAlgorithm.HS512)
            .compact();
    }

    /**
     * Get username dari JWT token
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }

    /**
     * Get userId dari JWT token
     */
    public Long getUserIdFromToken(String token) {
        Object userId = Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .get("userId");

        if (userId != null) {
            if (userId instanceof Integer) {
                return Long.valueOf((Integer) userId);
            } else if (userId instanceof Long) {
                return (Long) userId;
            }
        }
        return null;
    }

    /**
     * Get role dari JWT token
     */
    public String getRoleFromToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .get("role", String.class);
    }

    /**
     * Validate JWT token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException ex) {
            System.err.println("Invalid JWT token: {}" + ex.getMessage());
        } catch (ExpiredJwtException ex) {
            System.err.println("Expired JWT token: {}" + ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            System.err.println("Unsupported JWT token: {}" + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            System.err.println("JWT claims string is empty: {}" + ex.getMessage());
        } catch (SignatureException ex) {
            System.err.println("JWT signature validation failed: {}" + ex.getMessage());
        }
        return false;
    }

    /**
     * Get signing key untuk sign/verify JWT
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Check if token is expired
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();

            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * Get remaining time until expiration (in milliseconds)
     */
    public long getExpirationTime(String token) {
        try {
            Date expiration = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();

            return expiration.getTime() - System.currentTimeMillis();
        } catch (Exception e) {
            return 0;
        }
    }
}
