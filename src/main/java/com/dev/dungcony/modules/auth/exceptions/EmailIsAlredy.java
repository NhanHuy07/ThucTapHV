package com.dev.dungcony.modules.auth.exceptions;

import com.dev.dungcony.commons.exceptions.ConflictException;

public class EmailIsAlredy extends ConflictException {

    public EmailIsAlredy() {
        super("Conflict_email", "Email đã tồn tại");
    }
}
