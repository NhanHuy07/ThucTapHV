package com.dev.dungcony.modules.auth.dtos.req;

import com.dev.dungcony.modules.auth.enums.OtpType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SendOtpReq(
        @NotBlank(message = "email not blank")
        @Email(message = "incorrect email format")
        String email,
        OtpType otpType
) {
}
