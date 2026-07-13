package com.dev.dungcony.modules.users.services.interfaces.users;

import com.dev.dungcony.modules.users.dto.res.UserRes;

public interface UserCreateService {
    UserRes createUser(int accId);
}
