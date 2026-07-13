package com.dev.dungcony.modules.users.mappers;

import com.dev.dungcony.modules.users.dto.req.RankAddReq;
import com.dev.dungcony.modules.users.dto.res.RankRes;
import com.dev.dungcony.modules.users.entities.Rank;

public final class RankMapper {
    public static Rank toEntity(RankAddReq req) {
        Rank rank = new Rank();

        rank.setExpireTime(req.expireTime());
        rank.setType(req.type());
        rank.setLevel(req.type().ordinal() + 1);
        rank.setMinPurchase(req.minPurchase());

        return rank;
    }

    public static RankRes toRes(Rank req) {
        return new RankRes(
                req.getId(),
                req.getType(),
                req.getMinPurchase(),
                req.getExpireTime()
        );
    }
}
