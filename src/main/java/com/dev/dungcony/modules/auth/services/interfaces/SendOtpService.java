package com.dev.dungcony.modules.auth.services.interfaces;

public interface SendOtpService {
    // void sendOtpForgotPassword(String email);

    void sendOtpRegister(String email);

    void sendOtpChangeEmail(int accid, String username, String email);

}
