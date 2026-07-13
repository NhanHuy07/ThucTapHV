package com.dev.dungcony.modules.auth.exceptions;

import com.dev.dungcony.commons.exceptions.UnauthorException;

public class OtpExpire extends UnauthorException {
    public OtpExpire() {
        super("otp-exp", "Otp đã hết hạn");
    }
}
