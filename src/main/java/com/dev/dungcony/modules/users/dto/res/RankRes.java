package com.dev.dungcony.modules.users.dto.res;

import com.dev.dungcony.modules.users.enums.RankType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;

public record RankRes(
        @JsonIgnore
        Integer id,
        RankType type,
        BigDecimal minTotalPurchase,
        Long expireTime
) {
}
