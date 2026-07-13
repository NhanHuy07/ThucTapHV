package com.dev.dungcony.modules.users.dto.req;

import java.math.BigDecimal;

public record RankUpdateReq(
        BigDecimal newMinPurchase,
        Long newExpireTime
) {
}
