package com.dev.dungcony.modules.users.services.interfaces.rank;

import com.dev.dungcony.modules.users.dto.res.RankRes;
import com.dev.dungcony.modules.users.entities.Rank;
import com.dev.dungcony.modules.users.enums.RankType;

import java.math.BigDecimal;

public interface RankGetService {
    RankRes getRank(RankType type);

    Rank getByPurchase(BigDecimal purchase);
}
