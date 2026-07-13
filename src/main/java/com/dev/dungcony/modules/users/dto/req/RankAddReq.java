package com.dev.dungcony.modules.users.dto.req;

import com.dev.dungcony.modules.users.enums.RankType;

import java.math.BigDecimal;

public record RankAddReq(
        RankType type,
        BigDecimal minPurchase,
        Long expireTime
) {
}
