package com.dev.dungcony.modules.auth.services.interfaces;

import com.dev.dungcony.modules.auth.enums.Role;
import io.jsonwebtoken.Claims;

import java.util.UUID;

public interface JwtService {

    // generate
    String generateToken(int id, String username, Role role, UUID userUuid);

    String generateToken(int id, String email);

    // extract
    String extractUsername(String token);

    Integer extractUserId(String token);

    UUID extractUserUuid(String token);

    Role extractRole(String token);

    String extractEmail(String token);

    // validate
    boolean validateToken(String token);

    boolean isTokenExpired(String token);

    Claims extractAllClaims(String token);
}
