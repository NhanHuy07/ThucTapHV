package com.dev.dungcony.modules.auth.exceptions;

import com.dev.dungcony.commons.exceptions.NotFoundException;

public class AccountNotFound extends NotFoundException {
    public AccountNotFound() {
        super("Account not found");
    }

}
