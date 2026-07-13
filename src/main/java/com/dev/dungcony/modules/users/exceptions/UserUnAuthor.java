package com.dev.dungcony.modules.users.exceptions;

import com.dev.dungcony.commons.exceptions.UnauthorException;

public class UserUnAuthor extends UnauthorException {

    public UserUnAuthor() {
        super("401", "Unauthorized to access this resource");
    }

}
