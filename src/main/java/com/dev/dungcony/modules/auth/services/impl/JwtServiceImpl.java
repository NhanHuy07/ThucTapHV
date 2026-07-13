package com.dev.dungcony.modules.auth.services.impl;

import com.dev.dungcony.modules.auth.config.JwtConfig;
import com.dev.dungcony.modules.auth.enums.Role;
import com.dev.dungcony.modules.auth.services.interfaces.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class JwtServiceImpl implements JwtService {

    private final Key key;
    private final JwtConfig jwtConfig;

    public JwtServiceImpl(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        this.key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());
    }

    // ========================= Generate =========================
    @Override
    public String generateToken(int id, String username, Role role, UUID userUuid) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtConfig.getExpiration() * 1000L);

        var builder = Jwts.builder()
                .setSubject(String.valueOf(id))
                .claim("username", username)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiration);

        if (userUuid != null) {
            builder.claim("userUuid", userUuid.toString());
        }

        return builder.signWith(key, SignatureAlgorithm.HS512).compact();
    }

    @Override
    public String generateToken(int id, String email) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtConfig.getExpiration() * 1000L);

        return Jwts.builder()
                .setSubject(String.valueOf(id))
                .claim("email", email)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    // ========================= Extract =========================

    @Override
    public String extractUsername(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.get("username", String.class);
        } catch (Exception e) {
            log.error("Error extracting username from token: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Integer extractUserId(String token) {
        try {
            Claims claims = extractAllClaims(token);
            String subject = claims.getSubject();
            return Integer.parseInt(subject);
        } catch (Exception e) {
            log.error("Error extracting user ID from token: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public UUID extractUserUuid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            String raw = claims.get("userUuid", String.class);
            return raw != null ? UUID.fromString(raw) : null;
        } catch (Exception e) {
            log.error("Error extracting userUuid from token: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Role extractRole(String token) {
        try {
            Claims claims = extractAllClaims(token);
            String role = claims.get("role", String.class);
            return Role.valueOf(role);
        } catch (Exception e) {
            log.error("Error extracting role from token: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public String extractEmail(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.get("email", String.class);
        } catch (Exception e) {
            log.error("Error extracting email from token: {}", e.getMessage());
            return null;
        }
    }

    // ========================= Validate =========================

    @Override
    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            log.error("Error checking token expiration: {}", e.getMessage());
            return true;
        }
    }

    @Override
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
