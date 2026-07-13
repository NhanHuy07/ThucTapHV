package com.dev.dungcony.modules.users.exceptions;

import com.dev.dungcony.commons.exceptions.NotFoundException;

public class UserNotFound extends NotFoundException {

    public UserNotFound() {
        super("User not found");
    }

}
