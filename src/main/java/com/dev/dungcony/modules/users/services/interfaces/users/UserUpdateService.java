package com.dev.dungcony.modules.users.services.interfaces.users;

import com.dev.dungcony.modules.users.dto.res.UserRes;
import com.dev.dungcony.modules.users.dto.req.UserUpdateReq;

import java.math.BigDecimal;
import java.util.UUID;

public interface UserUpdateService {
    UserRes updateUser(Integer accId, UserUpdateReq req);

    UserRes adminUpdateUser(UserUpdateReq req);

    void increasePurchaseAndUpdateRank(UUID userId, BigDecimal amount);

    void reducePurchaseAndUpdateRank(UUID userId, BigDecimal amount);
}
