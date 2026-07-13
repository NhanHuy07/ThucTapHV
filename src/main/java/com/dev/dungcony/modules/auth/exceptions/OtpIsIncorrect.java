package com.dev.dungcony.modules.auth.exceptions;

import com.dev.dungcony.commons.exceptions.ConflictException;

public class OtpIsIncorrect extends ConflictException {
    public OtpIsIncorrect() {
        super("Otp không đúng");
    }
}
