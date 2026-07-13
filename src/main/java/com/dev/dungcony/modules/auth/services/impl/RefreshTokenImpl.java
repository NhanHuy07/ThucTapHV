package com.dev.dungcony.modules.auth.services.impl;

import com.dev.dungcony.modules.auth.config.JwtConfig;
import com.dev.dungcony.modules.auth.dtos.AccDto;
import com.dev.dungcony.modules.auth.entities.Account;
import com.dev.dungcony.modules.auth.exceptions.TokenInvalid;
import com.dev.dungcony.modules.auth.repositories.AccountRepository;
import com.dev.dungcony.modules.auth.services.interfaces.RedisService;
import com.dev.dungcony.modules.auth.services.interfaces.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class RefreshTokenImpl implements RefreshTokenService {

    private final RedisService redis;
    private final AccountRepository accountRepository;
    private final JwtConfig jwtConfig;

    /**
     * Create refresh token
     */
    @Override
    public String create(int accId, String deviceId) {

        long ttl = jwtConfig.getRefreshExpiration();

        // revoke old token of device
        String oldToken = redis.getValue(deviceKey(accId, deviceId));

        if (oldToken != null) {
            redis.delete(refreshKey(oldToken));
        }

        String token = UUID.randomUUID().toString();

        // token -> account
        redis.cache(
                refreshKey(token),
                String.valueOf(accId),
                ttl);

        // device -> token
        redis.cache(
                deviceKey(accId, deviceId),
                token,
                ttl);

        // account -> device
        redis.addToSet(
                devicesKey(accId),
                deviceId);

        redis.expire(devicesKey(accId), ttl);

        return token;
    }

    /**
     * Verify refresh token
     */
    @Override
    public AccDto verify(String refreshToken) {

        String accId = redis.getValue(refreshKey(refreshToken));

        if (accId == null)
            throw new TokenInvalid();

        Account acc = accountRepository
                .findById(Integer.parseInt(accId))
                .orElseThrow(TokenInvalid::new);

        return new AccDto(
                acc.getId(),
                acc.getEmail(),
                acc.getUsername(),
                acc.getPassword(),
                acc.getVerify(),
                acc.getRole(),
                acc.getStatus());
    }

    /**
     * Revoke token of specific device
     */
    @Override
    public void revoke(String refreshToken, String deviceId) {

        String accId = redis.getValue(refreshKey(refreshToken));

        if (accId == null)
            return;

        int accountId = Integer.parseInt(accId);

        redis.delete(refreshKey(refreshToken));
        redis.delete(deviceKey(accountId, deviceId));

        redis.removeFromSet(
                devicesKey(accountId),
                deviceId);
    }

    /**
     * Revoke all devices
     */
    @Override
    public void revokeAll(int accId) {

        Set<String> devices = redis.getMembers(devicesKey(accId));

        if (devices != null) {

            for (String deviceId : devices) {

                String token = redis.getValue(deviceKey(accId, deviceId));

                if (token != null) {
                    redis.delete(refreshKey(token));
                }

                redis.delete(deviceKey(accId, deviceId));
            }
        }

        redis.delete(devicesKey(accId));
    }

    /**
     * Redis Keys
     */

    private String refreshKey(String token) {
        return "refresh:" + token;
    }

    private String devicesKey(int accId) {
        return "user:" + accId + ":devices";
    }

    private String deviceKey(int accId, String deviceId) {
        return "user:" + accId + ":device:" + deviceId + ":refresh";
    }
}