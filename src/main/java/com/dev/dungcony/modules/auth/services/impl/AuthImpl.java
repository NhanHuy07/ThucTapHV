package com.dev.dungcony.modules.auth.services.impl;

import com.dev.dungcony.modules.auth.config.JwtConfig;
import com.dev.dungcony.modules.auth.dtos.AccDto;
import com.dev.dungcony.modules.auth.dtos.req.RegisReq;
import com.dev.dungcony.modules.auth.dtos.res.AcessTokenRes;
import com.dev.dungcony.modules.auth.dtos.res.LoginResult;
import com.dev.dungcony.modules.auth.exceptions.IncorrectInput;
import com.dev.dungcony.modules.auth.enums.Status;
import com.dev.dungcony.modules.auth.services.interfaces.*;
import com.dev.dungcony.modules.users.exceptions.UserNotFound;
import com.dev.dungcony.modules.users.services.interfaces.users.UserCreateService;
import com.dev.dungcony.modules.users.services.interfaces.users.UserGetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthImpl implements AuthService {

    private final AccountGetService accGetService;
    private final AccountCheckService accountCheckService;
    private final AccountCreateService accountCreateService;
    private final SendOtpService sendOtpService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtConfig jwtConfig;
    private final RefreshTokenService refreshTokenService;
    private final UserGetService userGetService;
    private final UserCreateService userCreateService;

    @Transactional
    @Override
    public void register(RegisReq req) {

        log.info("Registering account with email: {}, username: {}", req.email(), req.username());

        accountCheckService.existsByEmail(req.email());
        accountCheckService.existsByUsername(req.username());

        accountCreateService.createAccount(req.email(), req.username(), passwordEncoder.encode(req.password()));
        sendOtpService.sendOtpRegister(req.email());
    }

    @Override
    public LoginResult login(String username, String password, String deviceId) {

        log.info("Logging in account with username: {}", username);

        AccDto acc = accGetService.getByUsername(username);

        if (!passwordEncoder.matches(password, acc.password()))
            throw new IncorrectInput();

        if (!acc.verify())
            throw new IncorrectInput();

        if (acc.status() != Status.ACTIVE)
            throw new IncorrectInput(); // account bị khóa

        log.info("Login successful for account: {}", acc.username());

        String acessToken = jwtService.generateToken(acc.id(), acc.username(), acc.role(), resolveUserUuid(acc.id()));
        String refreshToken = refreshTokenService.create(acc.id(), deviceId);

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(jwtConfig.isRefreshCookieSecure())
                .path(jwtConfig.getRefreshCookiePath())
                .sameSite("Lax")
                .maxAge(jwtConfig.getRefreshExpiration())
                .build();
        log.info("Generated refresh token for account: {}", acc.username());

        return new LoginResult(acessToken, JwtConfig.headerPrefix, jwtConfig.getExpiration(), refreshCookie.toString());
    }

    @Override
    public AcessTokenRes refreshToken(String refreshToken) {
        AccDto acc = refreshTokenService.verify(refreshToken);
        String token = jwtService.generateToken(acc.id(), acc.username(), acc.role(), resolveUserUuid(acc.id()));

        return new AcessTokenRes(token, jwtConfig.getExpiration());
    }

    // -----PRIVATE-----//
    private UUID resolveUserUuid(int accId) {
        try {
            return userGetService.getUserByAccId(accId).id();
        } catch (UserNotFound e) {
            log.info("khởi tạo user cho accId: {}", accId);
            try {
                return userCreateService.createUser(accId).id();
            } catch (Exception ex) {
                log.warn("createUser failed (possible race condition), retrying get: {}", ex.getMessage());
                return userGetService.getUserByAccId(accId).id();
            }
        }
    }

    @Override
    public void logout(String refreshToken, String deviceId) {
        refreshTokenService.revoke(refreshToken, deviceId);
    }
}
