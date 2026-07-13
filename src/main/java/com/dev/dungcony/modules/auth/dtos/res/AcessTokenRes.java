package com.dev.dungcony.modules.auth.dtos.res;

import com.dev.dungcony.modules.auth.config.JwtConfig;

public record AcessTokenRes(
        String token,
        String header,
        long expiration
) {
    public AcessTokenRes(String token, long expiration) {
        this(token, JwtConfig.headerPrefix, expiration);
    }
}
