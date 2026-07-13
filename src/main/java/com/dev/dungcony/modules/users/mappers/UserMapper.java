package com.dev.dungcony.modules.users.mappers;

import com.dev.dungcony.modules.users.dto.res.RankRes;
import com.dev.dungcony.modules.users.dto.res.UserRes;
import com.dev.dungcony.modules.users.entities.User;

public final class UserMapper {

    public static UserRes toUserRes(User user) {
        return new UserRes(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getAvatar(),
                user.getToltalPurchase(),
                RankMapper.toRes(user.getRank())
        );
    }
}
