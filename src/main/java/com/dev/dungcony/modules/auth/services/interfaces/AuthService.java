package com.dev.dungcony.modules.auth.services.interfaces;

import com.dev.dungcony.modules.auth.dtos.req.RegisReq;
import com.dev.dungcony.modules.auth.dtos.res.AcessTokenRes;
import com.dev.dungcony.modules.auth.dtos.res.LoginResult;

public interface AuthService {
    void register(RegisReq req);

    LoginResult login(String username, String password, String deviceId);

    void logout(String refreshToken, String deviceId);

    AcessTokenRes refreshToken(String refreshToken);
}
