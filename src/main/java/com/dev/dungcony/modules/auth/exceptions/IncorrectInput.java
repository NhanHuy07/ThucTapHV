package com.dev.dungcony.modules.auth.exceptions;

import com.dev.dungcony.commons.exceptions.InvalidException;

public class IncorrectInput extends InvalidException {
    public IncorrectInput() {

        super("INCORRECT_INPUT", "username or password không đúng");
    }
}
