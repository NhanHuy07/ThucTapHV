package com.dev.dungcony.modules.auth.exceptions;

import com.dev.dungcony.commons.exceptions.ConflictException;

public class UserNameIsAlredy extends ConflictException {

    public UserNameIsAlredy() {
        super("Conflict_user_name", "username đã tồn tại");
    }
}
