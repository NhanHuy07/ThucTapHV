package com.dev.dungcony.modules.auth.exceptions;

import com.dev.dungcony.commons.exceptions.UnauthorException;

public class TokenExpire extends UnauthorException {

    public TokenExpire() {

        super("auth_token_expire", "token đã hết hạn");
    }

}
