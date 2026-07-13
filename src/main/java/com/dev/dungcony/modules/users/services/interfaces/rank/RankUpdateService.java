package com.dev.dungcony.modules.users.services.interfaces.rank;

import com.dev.dungcony.modules.users.dto.req.RankUpdateReq;
import com.dev.dungcony.modules.users.dto.res.RankRes;
import com.dev.dungcony.modules.users.enums.RankType;

public interface RankUpdateService {
    RankRes update(RankType type, RankUpdateReq req);
}
