package com.dev.dungcony.modules.auth.dtos.req;

public record VerifyOtpReq(
        String email,
        String otp
) {
}
