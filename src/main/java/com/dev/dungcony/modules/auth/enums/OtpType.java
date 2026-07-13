package com.dev.dungcony.modules.auth.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OtpType {
    // OTP 6 số
    REGISTER("otp:register"),
    FORGOT_PASSWORD("otp:forgot_password"),
    CHANGE_EMAIL("otp:change_email");
    @JsonValue
    private final String value;
}