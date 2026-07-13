package com.dev.dungcony.modules.users.dto.res;

import java.math.BigDecimal;
import java.util.UUID;

public record UserRes(
        UUID id,
        String firstName,
        String lastName,
        String avatar,
        BigDecimal totalPurchase,
        RankRes rank
) {
}
