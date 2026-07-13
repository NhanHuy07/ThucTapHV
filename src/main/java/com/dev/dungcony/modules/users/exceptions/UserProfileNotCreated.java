package com.dev.dungcony.modules.users.exceptions;

import com.dev.dungcony.commons.exceptions.ForbiddenException;

public class UserProfileNotCreated extends ForbiddenException {

    public UserProfileNotCreated() {
        super("USER_PROFILE_NOT_CREATED",
                "User profile has not been created yet. Please create your profile and refresh your token.");
    }
}
