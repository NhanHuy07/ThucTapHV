package com.dev.dungcony.modules.auth.services.interfaces;

import com.dev.dungcony.modules.auth.dtos.req.VerifyOtpReq;

public interface VerifyOtpService {
    void verifyOtpRegister(VerifyOtpReq req);

    void verifyOtpEmailChange(int accid, String username, String newEmail, String otp);
}
