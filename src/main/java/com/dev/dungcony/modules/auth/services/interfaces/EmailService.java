package com.dev.dungcony.modules.auth.services.interfaces;

public interface EmailService {
    void sendNewPassword(String email, String newPassword);

    void sendOtpRegis(String email, String otp);

    void sendOtpChangeEmail(String email, String otp);
}