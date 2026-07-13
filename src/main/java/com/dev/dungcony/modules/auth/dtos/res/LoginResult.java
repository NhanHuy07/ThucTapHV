package com.dev.dungcony.modules.auth.dtos.res;

public record LoginResult(
        String token,
        String type,
        long expired,
        String refreshToken) {
}
