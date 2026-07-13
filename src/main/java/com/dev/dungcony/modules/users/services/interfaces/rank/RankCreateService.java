package com.dev.dungcony.modules.users.services.interfaces.rank;

import com.dev.dungcony.modules.users.dto.req.RankAddReq;

public interface RankCreateService {
    String createRank(RankAddReq req);
}
