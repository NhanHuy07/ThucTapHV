package com.dev.dungcony.modules.auth.exceptions;

import com.dev.dungcony.commons.exceptions.UnauthorException;

public class TokenInvalid extends UnauthorException {
    public TokenInvalid() {
        super("TOKEN_INVALID", "token không hợp lệ");
    }
}
